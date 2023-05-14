package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.*;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.parser.core.models.ParseOptions;
import org.apache.commons.lang3.StringUtils;

import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.dto.V32APIDTO;
import org.wso2.carbon.apimgt.ctl.artifact.converter.dto.V42APIDTO;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIInfo;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42.V42APIInfo;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.APIDTO;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class APIInfoMappingUtil {
    public static void mapAPIInfo(APIInfo srcAPIInfo, APIInfo targetAPIInfo, String srcVersion, String targetVersion, String srcPath) throws CTLArtifactConversionException {
        if (srcVersion.equals(Constants.V320) && targetVersion.equals(Constants.V420)) {
            v32tov42APIInfo(srcAPIInfo, targetAPIInfo, srcPath);
        }
        //todo: implement other versions, if no matching conversion found(unlikely to happen), return the same list
    }

    public static void v32tov42APIInfo(APIInfo srcAPIInfo, APIInfo targetAPIInfo, String srcPath) throws CTLArtifactConversionException {
        JsonObject srcAPIInfoJson = srcAPIInfo.getApiInfo();
        if (srcAPIInfoJson != null && !srcAPIInfoJson.isEmpty()) {
            JsonObject targetAPIInfoJson = new JsonObject();
            addV32ToV42DirectMappings(srcAPIInfoJson, targetAPIInfoJson, srcPath);
            targetAPIInfo.setApiInfo(targetAPIInfoJson);

            //add deployment-environments config if 3.2.0 API is in published state
            if (Constants.PUBLISHED.equals(CommonUtil.readElementAsString(srcAPIInfoJson, "status"))) {
                JsonArray deploymentEnvironments = new JsonArray();
                CommonUtil.readElementAsJsonArray(srcAPIInfoJson, "environments").iterator().forEachRemaining(
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

    private static void addV32ToV42DirectMappings(JsonObject src, JsonObject target, String srcPath) throws CTLArtifactConversionException {
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
                CommonUtil.readElementAsString(src, "responseCache")));
        target.addProperty("cacheTimeout", src.get("cacheTimeout").getAsInt());
        target.addProperty("isDefaultVersion", src.get("isDefaultVersion").getAsBoolean());
        target.addProperty("enableSchemaValidation", src.get("enableSchemaValidation").getAsBoolean());

        populateAPIPropertyAsString(src, target, "type", "type");
        String transports = CommonUtil.readElementAsString(src, "transports");
        if (!StringUtils.isEmpty(transports)) {
            JsonArray transportsArray = new JsonArray();
            Arrays.stream(transports.split(",")).map(String::trim).forEach(transportsArray::add);
            target.add("transport", transportsArray);
        }

        String implementation = CommonUtil.readElementAsString(src, "implementation");
        APIDTO.EndpointImplementationTypeEnum endpointImplementationType = APIDTO.EndpointImplementationTypeEnum.ENDPOINT;
        if (!StringUtils.isEmpty(implementation)) {
            endpointImplementationType = APIDTO.EndpointImplementationTypeEnum
                    .fromValue(src.get("implementation").getAsString());
        }
        target.addProperty("endpointImplementationType", endpointImplementationType.toString());

        JsonArray categories = CommonUtil.readElementAsJsonArray(src, "apiCategories");
        if (categories != null && !categories.isEmpty()) {
            JsonArray categoryNamesArray = new JsonArray();
            categories.asList().forEach(category -> {
                JsonObject categoryObject = category.getAsJsonObject();
                String categoryName = CommonUtil.readElementAsString(categoryObject, "name");
                if (!StringUtils.isEmpty(categoryName)) {
                    categoryNamesArray.add(categoryName);
                }
            });
            target.add("categories", categoryNamesArray);
        }

        target.addProperty("gatewayVendor", Constants.GATEWAY_VENDOR_WSO2);
        target.addProperty("gatewayType", Constants.GATEWAY_TYPE_SYNAPSE);
        target.addProperty("enableSubscriberVerification", false);

        JsonArray tags = CommonUtil.readElementAsJsonArray(src, "tags");
        if (tags != null && !tags.isEmpty()) {
            target.add("tags", tags);
        }

        String endpointConfig = CommonUtil.readElementAsString(src, "endpointConfig");
        JsonObject endpointConfigObj = new Gson().fromJson(endpointConfig, JsonObject.class);
        target.add("endpointConfig", endpointConfigObj);

        JsonArray availableTiers = src.get("availableTiers").getAsJsonArray();
        JsonArray policies = new JsonArray();
        for (JsonElement element : availableTiers.asList()) {
            JsonObject tier = element.getAsJsonObject();
            String tierName = CommonUtil.readElementAsString(tier, "name");
            policies.add(tierName);
        }
        target.add("policies", policies);

        String apiSecurity = CommonUtil.readElementAsString(src, "apiSecurity");
        if (!StringUtils.isEmpty(apiSecurity)) {
            JsonArray apiSecurityArray = new JsonArray();
            Arrays.stream(apiSecurity.split(",")).map(String::trim).forEach(apiSecurityArray::add);
            target.add("securityScheme", apiSecurityArray);
        }

        populateBusinessInfo(src, target);
        populateVisibilityInfo(src, target);
        populateSubscriptionAvailabilityInfo(src, target);
        populateAccessControlInfo(src, target);
        populateCorsConfig(src, target);
        populateAdditionalProperties(src, target);
        populateUriTemplates(srcPath, target);
    }

    public static V42APIDTO v32DtoToV42Dto(V32APIDTO v32APIDTO) {
        V42APIDTO v42APIDTO = new V42APIDTO();
        v42APIDTO.setId(v32APIDTO.getId());
        v42APIDTO.setName(v32APIDTO.getName());
        v42APIDTO.setDescription(v32APIDTO.getDescription());
        v42APIDTO.setContext(v32APIDTO.getContext());
        v42APIDTO.setVersion(v32APIDTO.getVersion());
        v42APIDTO.setProvider(v32APIDTO.getProvider());
        v42APIDTO.setLifeCycleStatus(v32APIDTO.getLifeCycleStatus());
        v42APIDTO.setWsdlInfo(v32APIDTO.getWsdlInfo());
        v42APIDTO.setWsdlUrl(v32APIDTO.getWsdlUrl());
        v42APIDTO.setResponseCachingEnabled(v32APIDTO.isResponseCachingEnabled());
        v42APIDTO.setCacheTimeout(v32APIDTO.getCacheTimeout());
        v42APIDTO.setHasThumbnail(v32APIDTO.isHasThumbnail());
        v42APIDTO.setIsDefaultVersion(v32APIDTO.isIsDefaultVersion());
        v42APIDTO.setEnableSchemaValidation(v32APIDTO.isEnableSchemaValidation());
        v42APIDTO.setType(V42APIDTO.TypeEnum.valueOf(v32APIDTO.getType().toString()));
        v42APIDTO.setTransport(v32APIDTO.getTransport());
        v42APIDTO.setTags(v32APIDTO.getTags());
        v42APIDTO.setPolicies(v32APIDTO.getPolicies());
        v42APIDTO.setApiThrottlingPolicy(v32APIDTO.getApiThrottlingPolicy());
        v42APIDTO.setAuthorizationHeader(v32APIDTO.getAuthorizationHeader());
        v42APIDTO.setSecurityScheme(v32APIDTO.getSecurityScheme());
        v42APIDTO.setMaxTps(v32APIDTO.getMaxTps());
        v42APIDTO.setVisibility(V42APIDTO.VisibilityEnum.valueOf(v32APIDTO.getVisibility().toString()));
        v42APIDTO.setVisibleTenants(v32APIDTO.getVisibleTenants());
        v42APIDTO.setVisibleRoles(v32APIDTO.getVisibleRoles());
        v42APIDTO.setMediationPolicies(v32APIDTO.getMediationPolicies());
        v42APIDTO.setSubscriptionAvailability(V42APIDTO.SubscriptionAvailabilityEnum.valueOf(v32APIDTO.
                getSubscriptionAvailability().toString()));
        v42APIDTO.setSubscriptionAvailableTenants(v32APIDTO.getSubscriptionAvailableTenants());
        v42APIDTO.setMonetization(v32APIDTO.getMonetization());
        //additional properties
        v42APIDTO.setAccessControl(V42APIDTO.AccessControlEnum.valueOf(v32APIDTO.getAccessControl().toString()));
        v42APIDTO.setAccessControlRoles(v32APIDTO.getAccessControlRoles());
        v42APIDTO.setBusinessInformation(v32APIDTO.getBusinessInformation());
        v42APIDTO.setCorsConfiguration(v32APIDTO.getCorsConfiguration());
        v42APIDTO.setWorkflowStatus(v32APIDTO.getWorkflowStatus());
        v42APIDTO.setCreatedTime(v32APIDTO.getCreatedTime());
        v42APIDTO.setLastUpdatedTime(v32APIDTO.getLastUpdatedTime());
        //endpoint config
        v42APIDTO.setEndpointImplementationType(V42APIDTO.EndpointImplementationTypeEnum.valueOf(v32APIDTO.
                getEndpointImplementationType().toString()));
        v42APIDTO.setScopes(v32APIDTO.getScopes());
        v42APIDTO.setThreatProtectionPolicies(v32APIDTO.getThreatProtectionPolicies());
        v42APIDTO.setCategories(v32APIDTO.getCategories());
        v42APIDTO.setKeyManagers(v32APIDTO.getKeyManagers());

        v42APIDTO.setGatewayType(Constants.GATEWAY_TYPE_WSO2_SYNAPSE);
        v42APIDTO.setGatewayVendor(Constants.GATEWAY_VENDOR_WSO2);
        //advertise info : todo check with heshan

        return new V42APIDTO();
    }

    private static void populateUriTemplates(String srcPath, JsonObject target) throws CTLArtifactConversionException {
        String pathToMetaInfo = srcPath + File.separator + Constants.META_INFO_DIRECTORY;
        JsonObject swaggerJson = CommonUtil.convertMapToJsonObject(ConfigFileUtil.readConfigFileToMap(pathToMetaInfo,
                Constants.SWAGGER));

        OpenAPIParser parser = new OpenAPIParser();
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolveFully(true);
        OpenAPI openAPI = parser.readContents(swaggerJson.getAsJsonObject().toString(), null, parseOptions).getOpenAPI();

        JsonArray uriTemplates = new JsonArray();
        for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
            Map<PathItem.HttpMethod, Operation> operationsMap = entry.getValue().readOperationsMap();
            for (Map.Entry<PathItem.HttpMethod, Operation> operationEntry : operationsMap.entrySet()) {
                JsonObject uriTemplate = new JsonObject();
                Operation op = operationEntry.getValue();
                uriTemplate.addProperty("target", entry.getKey());
                uriTemplate.addProperty("verb", operationEntry.getKey().toString());

                Map<String, Object> extensions =  op.getExtensions();
                if (extensions != null) {
                    if (extensions.get("x-auth-type") != null) {
                        uriTemplate.addProperty("authType", extensions.get("x-auth-type").toString());
                    }
                    if (extensions.get("x-throttling-tier") != null) {
                        uriTemplate.addProperty("throttlingPolicy", extensions.get("x-throttling-tier").toString());
                    }
                }

                List<SecurityRequirement> requirements = op.getSecurity();
                for (SecurityRequirement requirement : requirements) {
                    if (requirement.get(Constants.DEFAULT_SECURITY) != null) {
                        List<String> defaultScopes = requirement.get(Constants.DEFAULT_SECURITY);
                        JsonArray scopes = new JsonArray();
                        for (String scope : defaultScopes) {
                            scopes.add(scope);
                        }
                        uriTemplate.add("scopes", scopes);
                    }
                }
                uriTemplates.add(uriTemplate);
            }
        }
        target.add("operations", uriTemplates);
    }

    private static void populateAPIPropertyAsString(JsonObject src, JsonObject target, String srcKey, String targetKey) {
        String value = CommonUtil.readElementAsString(src, srcKey);
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
        String businessOwner = CommonUtil.readElementAsString(src, "businessOwner");
        if (!StringUtils.isEmpty(businessOwner)) {
            businessInfo.addProperty("businessOwner", businessOwner);
        }
        String businessOwnerEmail = CommonUtil.readElementAsString(src, "businessOwnerEmail");
        if (!StringUtils.isEmpty(businessOwnerEmail)) {
            businessInfo.addProperty("businessOwnerEmail", businessOwnerEmail);
        }
        String technicalOwner = CommonUtil.readElementAsString(src, "technicalOwner");
        if (!StringUtils.isEmpty(technicalOwner)) {
            businessInfo.addProperty("technicalOwner", technicalOwner);
        }
        String technicalOwnerEmail = CommonUtil.readElementAsString(src, "technicalOwnerEmail");
        if (!StringUtils.isEmpty(technicalOwnerEmail)) {
            businessInfo.addProperty("technicalOwnerEmail", technicalOwnerEmail);
        }
        target.add("businessInformation", businessInfo);
    }

    private static void populateVisibilityInfo(JsonObject src, JsonObject target) {
        String visibility = CommonUtil.readElementAsString(src, "visibility").toUpperCase();
        if (!StringUtils.isEmpty(visibility)) {
            APIDTO.VisibilityEnum visibilityEnum = Enum.valueOf(APIDTO.VisibilityEnum.class, visibility);
            target.addProperty("visibility", visibilityEnum.toString());

            String visibleRoles = CommonUtil.readElementAsString(src, "visibleRoles");
            if (!StringUtils.isEmpty(visibleRoles)) {
                String[] rolesArray = CommonUtil.fromStringToArray(visibleRoles, ",");
                target.add("visibleRoles", CommonUtil.toJsonArray(rolesArray));
            }
            String visibleTenants = CommonUtil.readElementAsString(src, "visibleTenants");
            if (!StringUtils.isEmpty(visibleTenants)) {
                String[] tenantsArray = CommonUtil.fromStringToArray(visibleTenants, ",");
                target.add("visibleTenants", CommonUtil.toJsonArray(tenantsArray));
            }
        }
    }

    private static void populateSubscriptionAvailabilityInfo(JsonObject src, JsonObject target) {
        String subscriptionAvailability = CommonUtil.readElementAsString(src, "subscriptionAvailability").toUpperCase();
        if (!StringUtils.isEmpty(subscriptionAvailability)) {
            APIDTO.SubscriptionAvailabilityEnum subscriptionAvailabilityEnum =
                    APIDTO.SubscriptionAvailabilityEnum.valueOf(subscriptionAvailability);
            target.addProperty("subscriptionAvailability", subscriptionAvailabilityEnum.toString());

            String subscriptionAvailableTenants = CommonUtil.readElementAsString(src, "subscriptionAvailableTenants");
            if (!StringUtils.isEmpty(subscriptionAvailableTenants)) {
                String[] tenants = CommonUtil.fromStringToArray(subscriptionAvailableTenants, ",");
                target.add("subscriptionAvailableTenants", CommonUtil.toJsonArray(tenants));
            }
        }
    }

    private static void populateAccessControlInfo(JsonObject src, JsonObject target) {
        String accessControl = CommonUtil.readElementAsString(src, "accessControl");
        if (!StringUtils.isEmpty(accessControl)) {
            APIDTO.AccessControlEnum accessControlEnum;
            if (Constants.ACCESS_CONTROL_ALL.equals(accessControl)) {
                accessControlEnum = APIDTO.AccessControlEnum.NONE;
            } else {
                accessControlEnum = APIDTO.AccessControlEnum.RESTRICTED;
                String allowRoles = CommonUtil.readElementAsString(src, "accessControlRoles");
                if (!StringUtils.isEmpty(allowRoles)) {
                    String[] allowRolesArray = CommonUtil.fromStringToArray(allowRoles, ",");
                    target.add("accessControlRoles", CommonUtil.toJsonArray(allowRolesArray));
                }
            }
            target.addProperty("accessControl", accessControlEnum.toString());
        }
    }

    private static void populateCorsConfig(JsonObject src, JsonObject target) {
        JsonObject corsConfig = src.get("corsConfiguration") != null ? src.get("corsConfiguration").getAsJsonObject() : null;
        if (corsConfig != null) {
            JsonObject cors = new JsonObject();
            boolean corsConfigurationEnabled = CommonUtil.readElementAsBoolean(corsConfig, "corsConfigurationEnabled");
            JsonArray accessControlAllowOrigins = CommonUtil.readElementAsJsonArray(corsConfig, "accessControlAllowOrigins");
            boolean accessControlAllowCredentials = CommonUtil.readElementAsBoolean(corsConfig, "accessControlAllowCredentials");
            JsonArray accessControlAllowHeaders = CommonUtil.readElementAsJsonArray(corsConfig, "accessControlAllowHeaders");
            JsonArray accessControlAllowMethods = CommonUtil.readElementAsJsonArray(corsConfig, "accessControlAllowMethods");

            cors.addProperty("corsConfigurationEnabled", corsConfigurationEnabled);
            cors.add("accessControlAllowOrigins", accessControlAllowOrigins);
            cors.addProperty("accessControlAllowCredentials", accessControlAllowCredentials);
            cors.add("accessControlAllowHeaders", accessControlAllowHeaders);
            cors.add("accessControlAllowMethods", accessControlAllowMethods);

            target.add("corsConfiguration", cors);
        }
    }
}
