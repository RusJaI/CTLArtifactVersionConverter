package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIInfo;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42.V42APIInfo;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIDTO;

import java.util.Arrays;

public class APIInfoMappingUtil {
    public static void mapAPIInfo(APIInfo srcAPIInfo, APIInfo targetAPIInfo, String srcVersion, String targetVersion) {
        if (srcVersion.equals(Constants.V320) && targetVersion.equals(Constants.V420)) {
            v32tov42APIInfo(srcAPIInfo, targetAPIInfo);
        }
        //todo: implement other versions, if no matching conversion found(unlikely to happen), return the same list
    }

    public static void v32tov42APIInfo(APIInfo srcAPIInfo, APIInfo targetAPIInfo) {
        JsonObject srcAPIInfoJson = srcAPIInfo.getApiInfo();
        if (srcAPIInfoJson != null && !srcAPIInfoJson.isEmpty()) {
            JsonObject targetAPIInfoJson = new JsonObject();
            addV32ToV42DirectMappings(srcAPIInfoJson, targetAPIInfoJson);
            targetAPIInfo.setApiInfo(targetAPIInfoJson);

            //add deployment-environments config if 3.2.0 API is in published state
            if (Constants.PUBLISHED.equals(readElementAsString(srcAPIInfoJson, "status"))) {
                JsonArray deploymentEnvironments = new JsonArray();
                readElementAsJsonArray(srcAPIInfoJson, "environments").iterator().forEachRemaining(
                        environment -> {
                            JsonObject environmentObject = new JsonObject();
                            //environmentObject.addProperty("deploymentEnvironment", environment.getAsString());
                            environmentObject.addProperty("deploymentEnvironment", "Default");
                            environmentObject.addProperty("displayOnDevportal", true);
                            deploymentEnvironments.add(environmentObject);
                        }
                );
                ((V42APIInfo)targetAPIInfo).setDeploymentEnvironments(deploymentEnvironments);
            }
        }
    }

    private static void addV32ToV42DirectMappings(JsonObject src, JsonObject target) {
        JsonObject idObject = src.get("id").getAsJsonObject();

        populateAPIPropertyAsString(src, target, "uuid", "id");
        populateAPIPropertyAsString(idObject, target, "providerName", "provider");
        populateAPIPropertyAsString(idObject, target, "apiName", "name");
        populateAPIPropertyAsString(idObject, target, "version", "version");
        populateAPIPropertyAsString(src, target, "description", "description");
        populateAPIPropertyAsString(src, target, "context", "context");
        populateAPIPropertyAsString(src, target, "status", "lifeCycleStatus");
        populateAPIPropertyAsString(src, target, "authorizationHeader", "authorizationHeader");

        target.addProperty("responseCachingEnabled", Constants.V32_RESPONSE_CACHING_ENABLED.equals(
                readElementAsString(src, "responseCache")));
        target.addProperty("cacheTimeout", src.get("cacheTimeout").getAsInt());
        target.addProperty("isDefaultVersion", src.get("isDefaultVersion").getAsBoolean());
        target.addProperty("enableSchemaValidation", src.get("enableSchemaValidation").getAsBoolean());

        populateAPIPropertyAsString(src, target, "type", "type");
        String transports = readElementAsString(src, "transports");
        if (!StringUtils.isEmpty(transports)) {
            JsonArray transportsArray = new JsonArray();
            Arrays.stream(transports.split(",")).map(String::trim).forEach(transportsArray::add);
            target.add("transport", transportsArray);
        }

        String implementation = readElementAsString(src, "implementation");
        APIDTO.EndpointImplementationTypeEnum endpointImplementationType = APIDTO.EndpointImplementationTypeEnum.ENDPOINT;
        if (!StringUtils.isEmpty(implementation)) {
            endpointImplementationType = APIDTO.EndpointImplementationTypeEnum
                    .fromValue(src.get("implementation").getAsString());
        }
        target.addProperty("endpointImplementationType", endpointImplementationType.toString());

        JsonArray categories = readElementAsJsonArray(src, "apiCategories");
        if (categories != null && !categories.isEmpty()) {
            JsonArray categoryNamesArray = new JsonArray();
            categories.asList().forEach(category -> {
                JsonObject categoryObject = category.getAsJsonObject();
                String categoryName = readElementAsString(categoryObject, "name");
                if (!StringUtils.isEmpty(categoryName)) {
                    categoryNamesArray.add(categoryName);
                }
            });
            target.add("apiCategories", categoryNamesArray);
        }

        target.addProperty("gatewayVendor", Constants.GATEWAY_VENDOR_WSO2);
        target.addProperty("gatewayType", Constants.GATEWAY_TYPE_SYNAPSE);
        target.addProperty("enableSubscriberVerification", false);

        JsonArray tags = readElementAsJsonArray(src, "tags");
        if (tags != null && !tags.isEmpty()) {
            target.add("tags", tags);
        }

        String endpointConfig = readElementAsString(src, "endpointConfig");
        JsonObject endpointConfigObj = new Gson().fromJson(endpointConfig, JsonObject.class);
        target.add("endpointConfig", endpointConfigObj);

        JsonArray availableTiers = src.get("availableTiers").getAsJsonArray();
        JsonArray policies = new JsonArray();
        for (JsonElement element : availableTiers.asList()) {
            JsonObject tier = element.getAsJsonObject();
            String tierName = readElementAsString(tier, "name");
            policies.add(tierName);
        }
        target.add("policies", policies);

        populateBusinessInfo(src, target);
        populateVisibilityInfo(src, target);
        populateSubscriptionAvailabilityInfo(src, target);
        populateAccessControlInfo(src, target);
        populateCorsConfig(src, target);
        populateAdditionalProperties(src, target);
    }

    private static void populateAPIPropertyAsString(JsonObject src, JsonObject target, String srcKey, String targetKey) {
        String value = readElementAsString(src, srcKey);
        if (!StringUtils.isEmpty(value)) {
            target.addProperty(targetKey, value);
        }
    }

    private static void populateAdditionalProperties(JsonObject src, JsonObject target) {
        JsonObject properties = src.get("additionalProperties").getAsJsonObject();
        JsonArray propertiesArray = new JsonArray();
        JsonObject propertiesMap = new JsonObject();
        if (properties != null & !properties.isEmpty()) {
            for (String key : properties.keySet()) {
                String value = properties.get(key).getAsString();
                if (value != null) {
                    JsonObject property = new JsonObject();
                    property.addProperty("name", key.replace("__display", ""));
                    property.addProperty("value", value);
                    property.addProperty("display", key.contains("__display"));
                    propertiesArray.add(property);

                    propertiesMap.add(key, property);
                }
            }
            target.add("additionalProperties", propertiesArray);
            target.add("additionalPropertiesMap", propertiesMap);
        }
    }

    private static void populateBusinessInfo(JsonObject src, JsonObject target) {
        JsonObject businessInfo = new JsonObject();
        String businessOwner = readElementAsString(src, "businessOwner");
        if (!StringUtils.isEmpty(businessOwner)) {
            businessInfo.addProperty("businessOwner", businessOwner);
        }
        String businessOwnerEmail = readElementAsString(src, "businessOwnerEmail");
        if (!StringUtils.isEmpty(businessOwnerEmail)) {
            businessInfo.addProperty("businessOwnerEmail", businessOwnerEmail);
        }
        String technicalOwner = readElementAsString(src, "technicalOwner");
        if (!StringUtils.isEmpty(technicalOwner)) {
            businessInfo.addProperty("technicalOwner", technicalOwner);
        }
        String technicalOwnerEmail = readElementAsString(src, "technicalOwnerEmail");
        if (!StringUtils.isEmpty(technicalOwnerEmail)) {
            businessInfo.addProperty("technicalOwnerEmail", technicalOwnerEmail);
        }
        target.add("businessInformation", businessInfo);
    }

    private static void populateVisibilityInfo(JsonObject src, JsonObject target) {
        String visibility = readElementAsString(src, "visibility").toUpperCase();
        if (!StringUtils.isEmpty(visibility)) {
            APIDTO.VisibilityEnum visibilityEnum = Enum.valueOf(APIDTO.VisibilityEnum.class, visibility);
            target.addProperty("visibility", visibilityEnum.toString());

            String visibleRoles = readElementAsString(src, "visibleRoles");
            if (!StringUtils.isEmpty(visibleRoles)) {
                String[] rolesArray = fromStringToArray(visibleRoles, ",");
                target.add("visibleRoles", toJsonArray(rolesArray));
            }
            String visibleTenants = readElementAsString(src, "visibleTenants");
            if (!StringUtils.isEmpty(visibleTenants)) {
                String[] tenantsArray = fromStringToArray(visibleTenants, ",");
                target.add("visibleTenants", toJsonArray(tenantsArray));
            }
        }
    }

    private static void populateSubscriptionAvailabilityInfo(JsonObject src, JsonObject target) {
        String subscriptionAvailability = readElementAsString(src, "subscriptionAvailability").toUpperCase();
        if (!StringUtils.isEmpty(subscriptionAvailability)) {
            APIDTO.SubscriptionAvailabilityEnum subscriptionAvailabilityEnum =
                    APIDTO.SubscriptionAvailabilityEnum.valueOf(subscriptionAvailability);
            target.addProperty("subscriptionAvailability", subscriptionAvailabilityEnum.toString());

            String subscriptionAvailableTenants = readElementAsString(src, "subscriptionAvailableTenants");
            if (!StringUtils.isEmpty(subscriptionAvailableTenants)) {
                String[] tenants = fromStringToArray(subscriptionAvailableTenants, ",");
                target.add("subscriptionAvailableTenants", toJsonArray(tenants));
            }
        }
    }

    private static void populateAccessControlInfo(JsonObject src, JsonObject target) {
        String accessControl = readElementAsString(src, "accessControl");
        if (!StringUtils.isEmpty(accessControl)) {
            APIDTO.AccessControlEnum accessControlEnum;
            if (Constants.ACCESS_CONTROL_ALL.equals(accessControl)) {
                accessControlEnum = APIDTO.AccessControlEnum.NONE;
            } else {
                accessControlEnum = APIDTO.AccessControlEnum.RESTRICTED;
                String allowRoles = readElementAsString(src, "accessControlRoles");
                if (!StringUtils.isEmpty(allowRoles)) {
                    String[] allowRolesArray = fromStringToArray(allowRoles, ",");
                    target.add("accessControlRoles", toJsonArray(allowRolesArray));
                }
            }
            target.addProperty("accessControl", accessControlEnum.toString());
        }
    }

    private static void populateCorsConfig(JsonObject src, JsonObject target) {
        JsonObject corsConfig = src.get("corsConfiguration") != null ? src.get("corsConfiguration").getAsJsonObject() : null;
        if (corsConfig != null) {
            JsonObject cors = new JsonObject();
            boolean corsConfigurationEnabled =  readElementAsBoolean(corsConfig, "corsConfigurationEnabled");
            JsonArray accessControlAllowOrigins = readElementAsJsonArray(corsConfig, "accessControlAllowOrigins");
            boolean accessControlAllowCredentials = readElementAsBoolean(corsConfig, "accessControlAllowCredentials");
            JsonArray accessControlAllowHeaders = readElementAsJsonArray(corsConfig, "accessControlAllowHeaders");
            JsonArray accessControlAllowMethods = readElementAsJsonArray(corsConfig, "accessControlAllowMethods");

            cors.addProperty("corsConfigurationEnabled", corsConfigurationEnabled);
            cors.add("accessControlAllowOrigins", accessControlAllowOrigins);
            cors.addProperty("accessControlAllowCredentials", accessControlAllowCredentials);
            cors.add("accessControlAllowHeaders", accessControlAllowHeaders);
            cors.add("accessControlAllowMethods", accessControlAllowMethods);

            target.add("corsConfiguration", cors);
        }
    }

    private static String readElementAsString(JsonObject object, String key) {
        String value = null;
        JsonElement element = object.get(key);
        if (element != null) {
            value = element.getAsString();
        }
        return value;
    }

    private static boolean readElementAsBoolean(JsonObject object, String key) {
        boolean value = false;
        JsonElement element = object.get(key);
        if (element != null) {
            value = element.getAsBoolean();
        }
        return value;
    }

    private static JsonArray readElementAsJsonArray(JsonObject object, String key) {
        JsonArray value = null;
        JsonElement element = object.get(key);
        if (element != null) {
            value = element.getAsJsonArray();
        }
        return value;
    }

    private static String[] fromStringToArray(String str, String delimiter) {
        if (StringUtils.isEmpty(str)) {
            return new String[0];
        } else {
            return Arrays.stream(str.split(delimiter)).map(String::trim).toArray(String[]::new);
        }
    }

    private static JsonArray toJsonArray(String[] array) {
        JsonArray jsonArray = new JsonArray();
        Arrays.stream(array).map(String::trim).forEach(jsonArray::add);
        return jsonArray;
    }

}
