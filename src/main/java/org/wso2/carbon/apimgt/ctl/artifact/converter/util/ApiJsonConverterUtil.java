package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiJsonConverterUtil {

    /**
     * Initialize gson to serialize with null values
     */
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static JsonObject convertFromV32To42(Map apiMap, JsonObject paramsObject, boolean isApiProduct)
            throws ParseException {

        Map <String, Object> dataMap = new HashMap<String, Object>();

        JsonObject artifactObject = new JsonObject();
        artifactObject.addProperty("type", isApiProduct? "api_product" : "api");
        artifactObject.addProperty("version", "v4.2.0");
        /**
         * Map contents of Id object
         */
        JsonObject idObject = apiMap.get("id") == null ? null : gson.toJsonTree(apiMap.get("id")).getAsJsonObject();

        if (idObject != null) {
            dataMap.put("id", apiMap.get("uuid") == null ? null : apiMap.get("uuid"));
            dataMap.put("name", idObject.has("apiName") ? idObject.get("apiName")
                    : null);
            dataMap.put("version", idObject.has("version") ? idObject.get("version")
                    : null);
            dataMap.put("provider", idObject.has("providerName") ? idObject.get("providerName")
                    : null);
        }

        dataMap.put("context", apiMap.get("context"));
        dataMap.put("tags", apiMap.get("tags"));

        String dateTimeString = apiMap.get("lastUpdated") == null ? null : apiMap.get("lastUpdated").toString();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
        SimpleDateFormat newFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");
        Date latestUpdatedDate = formatter.parse(dateTimeString);
        dataMap.put("lastUpdatedTimestamp", latestUpdatedDate.getTime());
        dataMap.put("lastUpdatedTime", newFormatter.format(latestUpdatedDate));

        /**
         * Map availableTiers
         */
        JsonArray tiersArray = apiMap.get("availableTiers") == null ? null :
                gson.toJsonTree(apiMap.get("availableTiers")).getAsJsonArray();
        List<String> policiesList = new ArrayList<String>();
        if (tiersArray != null) {
            for (JsonElement tierElement : tiersArray) {
                JsonObject tierObject = tierElement.getAsJsonObject();
                if (tierObject.has("name")) {
                    policiesList.add(tierObject.get("name").getAsString());
                }
            }
        }
        dataMap.put("policies", policiesList);
        dataMap.put("authorizationHeader", "Authorization");

        /**
         * Map business Info
         */
        JsonObject businessInfoJson = new JsonObject();
        if (apiMap.get(Constants.AJ_API_BUSINESS_OWNER) != null) {
            businessInfoJson.addProperty(Constants.AJ_API_BUSINESS_OWNER,
                    apiMap.get(Constants.AJ_API_BUSINESS_OWNER).toString());
        }
        if (apiMap.get(Constants.AJ_API_BUSINESS_OWNER_EMAIL) != null) {
            businessInfoJson.addProperty(Constants.AJ_API_BUSINESS_OWNER_EMAIL,
                    apiMap.get(Constants.AJ_API_BUSINESS_OWNER_EMAIL).toString());
        }
        if (apiMap.get(Constants.AJ_API_TECHNICAL_OWNER) != null) {
            businessInfoJson.addProperty(Constants.AJ_API_TECHNICAL_OWNER,
                    apiMap.get(Constants.AJ_API_TECHNICAL_OWNER).toString());
        }
        if (apiMap.get(Constants.AJ_API_TECHNICAL_OWNER_EMAIL) != null) {
            businessInfoJson.addProperty(Constants.AJ_API_TECHNICAL_OWNER_EMAIL,
                    apiMap.get(Constants.AJ_API_TECHNICAL_OWNER_EMAIL).toString());
        }
        dataMap.put(Constants.AJ_BUSINESS_INFORMATION, businessInfoJson);
        dataMap.put("visibility", apiMap.get("visibility").toString().toUpperCase());
        dataMap.put("visibleRoles", new JsonArray());
        dataMap.put("visibleTenants", new JsonArray());
        if (apiMap.get("transports") != null) {
            dataMap.put("transport", apiMap.get("transports").toString().split(","));
        }
        dataMap.put("subscriptionAvailability", apiMap.get("subscriptionAvailability"));
        dataMap.put("subscriptionAvailableTenants", new JsonArray());
        dataMap.put("corsConfiguration", apiMap.get("corsConfiguration"));

        if ((apiMap.get("responseCache") != null) &&
                (apiMap.get("responseCache").toString().equalsIgnoreCase("Enabled"))) {
            dataMap.put("responseCachingEnabled", true);
        } else {
            dataMap.put("responseCachingEnabled", false);
        }

        if (apiMap.get("cacheTimeout") != null) {
            dataMap.put("cacheTimeout", Integer.parseInt(apiMap.get("cacheTimeout").toString()));
        }

        dataMap.put("hasThumbnail", false);

        if (apiMap.get("scopes") == null) {
            dataMap.put("scopes", new JsonArray());
        } else {
            dataMap.put("scopes", apiMap.get("scopes"));
        }

        if (apiMap.get(Constants.AJ_IS_DEFAULT_VERSION) == null) {
            dataMap.put(Constants.AJ_IS_DEFAULT_VERSION, false);
        } else {
            dataMap.put(Constants.AJ_IS_DEFAULT_VERSION, apiMap.get(Constants.AJ_IS_DEFAULT_VERSION));
        }

        dataMap.put(Constants.AJ_IS_REVISION, false);
        dataMap.put(Constants.AJ_REVISION_ID, 0);

        if (apiMap.get(Constants.AJ_kEY_MANAGERS) == null) {
            dataMap.put(Constants.AJ_kEY_MANAGERS, new JsonArray());
        } else {
            dataMap.put(Constants.AJ_kEY_MANAGERS, apiMap.get(Constants.AJ_kEY_MANAGERS));
        }

        if (apiMap.get("createdTime") != null) {
            dataMap.put("createdTime", apiMap.get("createdTime"));
        }

        /**
         * Map additionalProperties object to array and Add additionalPropertiesMap which is new in v42
         */
        JsonObject srcAdditionalPropertyJson = apiMap.get(Constants.AJ_ADDITIONAL_PROPERTIES) == null ? null :
                gson.toJsonTree(apiMap.get(Constants.AJ_ADDITIONAL_PROPERTIES)).getAsJsonObject();
        JsonArray additionalPropertyJsonArray = new JsonArray();
        JsonObject additionalPropertiesMap = new JsonObject();
        if (srcAdditionalPropertyJson != null && !srcAdditionalPropertyJson.isEmpty()) {
            for (String key : srcAdditionalPropertyJson.keySet()) {
                String value = srcAdditionalPropertyJson.get(key).getAsString();
                JsonObject property = new JsonObject();
                property.addProperty("name", key.replace("__display", ""));
                property.addProperty("value", value);
                property.addProperty("display", key.contains("__display"));
                additionalPropertyJsonArray.add(property);

                //when adding to map display is set to false(default), otherwise two properties will be added to the API
                JsonObject mapProperty = new JsonObject();
                mapProperty.addProperty("name", key.replace("__display", ""));
                mapProperty.addProperty("value", value);
                mapProperty.addProperty("display", false);
                additionalPropertiesMap.add(key, mapProperty);
            }
        }

        dataMap.put(Constants.AJ_ADDITIONAL_PROPERTIES, additionalPropertyJsonArray);
        dataMap.put(Constants.AJ_ADDITIONAL_PROPERTIES_MAP, additionalPropertiesMap);

        JsonObject monetizationObject = new JsonObject();
        if (apiMap.get(Constants.AJ_IS_MONETIZATION_ENABLED) == null) {
            monetizationObject.addProperty(Constants.AJ_IS_MONETIZATION_ENABLED, false);
        } else {
            monetizationObject.addProperty(Constants.AJ_IS_MONETIZATION_ENABLED,
                    apiMap.get(Constants.AJ_IS_MONETIZATION_ENABLED).toString());
        }
        if (apiMap.get(Constants.AJ_MONETIZATION_PROPERTIES) == null) {
            monetizationObject.add(Constants.AJ_MONETIZATION_PROPERTIES, new JsonObject());
        } else  {
            monetizationObject.add(Constants.AJ_MONETIZATION_PROPERTIES,
                    convertMapToJsonObject((Map) apiMap.get(Constants.AJ_MONETIZATION_PROPERTIES)));
        }
        dataMap.put("monetization", monetizationObject);

        String apiSecurityString = apiMap.get("apiSecurity") == null ? null : apiMap.get("apiSecurity").toString();
        if (StringUtils.isEmpty(apiSecurityString)) {
            dataMap.put("securityScheme", new JsonArray());
        } else {
            dataMap.put("securityScheme", apiSecurityString.split(","));
        }

        if (apiMap.get(Constants.AJ_ENABLE_SCHEMA_VALIDATION) == null) {
            dataMap.put(Constants.AJ_ENABLE_SCHEMA_VALIDATION, false);
        } else {
            dataMap.put(Constants.AJ_ENABLE_SCHEMA_VALIDATION,
                    apiMap.get(Constants.AJ_ENABLE_SCHEMA_VALIDATION));
        }

        dataMap.put("categories", apiMap.get("apiCategories"));

        if (apiMap.get(Constants.AJ_ACCESS_CONTROL) != null) {
            dataMap.put(Constants.AJ_ACCESS_CONTROL, apiMap.get(Constants.AJ_ACCESS_CONTROL));
        }
        dataMap.put("accessControlRoles", new JsonArray());
        dataMap.put(Constants.AJ_GATEWAY_VENDOR, Constants.GATEWAY_VENDOR_WSO2);

/***------------------------------------------------------------------------------------------------------------------*/

        if (isApiProduct) {
            dataMap.put("state", apiMap.get("status"));


        } else {
            dataMap.put("enableSubscriberVerification", false);
            dataMap.put("type", apiMap.get("type"));
            dataMap.put("lifeCycleStatus", apiMap.get("status"));

            /**
             * Map API Policies
             */
            JsonObject apiPoliciesObject = new JsonObject();
            if (apiMap.get("inSequence") != null) {
                JsonObject inObject = new JsonObject();
                inObject.addProperty(Constants.AJ_POLICY_NAME, apiMap.get("inSequence").toString());
                inObject.addProperty(Constants.AJ_POLICY_VERSION, "v1");
                inObject.add(Constants.AJ_POLICY_ID, null);
                inObject.add(Constants.AJ_POLICY_PARAMS, new JsonObject());
                JsonArray policyArray = new JsonArray();
                policyArray.add(inObject);
                apiPoliciesObject.add("request", policyArray);
            }

            if (apiMap.get("outSequence") != null) {
                JsonObject outObject = new JsonObject();
                outObject.addProperty(Constants.AJ_POLICY_NAME, apiMap.get("outSequence").toString());
                outObject.addProperty(Constants.AJ_POLICY_VERSION, "v1");
                outObject.add(Constants.AJ_POLICY_ID, null);
                outObject.add(Constants.AJ_POLICY_PARAMS, new JsonObject());
                JsonArray policyArray = new JsonArray();
                policyArray.add(outObject);
                apiPoliciesObject.add("response", policyArray);
            }

            if (apiMap.get("faultSequence") != null) {
                JsonObject faultObject = new JsonObject();
                faultObject.addProperty(Constants.AJ_POLICY_NAME, apiMap.get("faultSequence").toString());
                faultObject.addProperty(Constants.AJ_POLICY_VERSION, "v1");
                faultObject.add(Constants.AJ_POLICY_ID, null);
                faultObject.add(Constants.AJ_POLICY_PARAMS, new JsonObject());
                JsonArray policyArray = new JsonArray();
                policyArray.add(faultObject);
                apiPoliciesObject.add("fault", policyArray);
            }
            dataMap.put("apiPolicies", apiPoliciesObject);

            /**
             * Map endpointConfig
             */
            JsonObject endpointConfigJson = apiMap.get("endpointConfig") == null ? null :
                    gson.fromJson(apiMap.get("endpointConfig").toString(), JsonObject.class);
            dataMap.put("endpointConfig", endpointConfigJson);

            if (apiMap.get("implementation") != null) {
                dataMap.put("endpointImplementationType", apiMap.get("implementation"));
            }
            dataMap.put("mediationPolicies", new JsonArray());

            JsonObject websubObject = new JsonObject();
            websubObject.addProperty("enable", false);
            websubObject.addProperty("secret", "");
            websubObject.addProperty("signingAlgorithm", "SHA1");
            websubObject.addProperty("signatureHeader", "x-hub-signature");
            dataMap.put("websubSubscriptionConfiguration", websubObject);

            JsonObject advertiseInfoObject = new JsonObject();
            advertiseInfoObject.addProperty("advertised", false);
            advertiseInfoObject.add("apiOwner", idObject == null ? null : idObject.get("providerName"));
            advertiseInfoObject.addProperty("vendor", "WSO2");
            dataMap.put("advertiseInfo", advertiseInfoObject);
            dataMap.put(Constants.AJ_GATEWAY_TYPE, Constants.GATEWAY_TYPE_WSO2_SYNAPSE);
            dataMap.put(Constants.AJ_ASYNC_TRANSPORT_PROTOCOLS, new String[]{"http", "https"});
            dataMap.put("organizationId", "carbon.super");
        }

        /**
         * Convert model to JsonObject
         */
        JsonObject dataJsonObject = convertMapToJsonObject(dataMap);

        /**
         * Update values received from paramJson
         */
        dataJsonObject = setFromParamsJson(paramsObject, dataJsonObject);
        artifactObject.add("data", dataJsonObject);
        return artifactObject;
    }

    private static JsonObject convertMapToJsonObject(Map targetModel) {
        JsonObject model = gson.toJsonTree(targetModel, targetModel.getClass()).getAsJsonObject();
        return model;
    }

    private static JsonObject setFromParamsJson(JsonObject paramsJson, JsonObject targetModelJsonObject) {
        Set<String> keySet = paramsJson.keySet();
        if (!keySet.isEmpty()) {
            Set<String> targetFields = targetModelJsonObject.keySet();
            for (String key : keySet) {
                if (targetFields.contains(key)) {
                    targetModelJsonObject.add(key, paramsJson.get(key));
                }
            }
        }
        return  targetModelJsonObject;
    }


}
