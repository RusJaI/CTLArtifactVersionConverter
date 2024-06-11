package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ApiJsonConverterUtil {

    /**
     * Initialize gson to serialize with null values
     */
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static JsonObject convertFromV32To42(Map apiMap, JsonObject paramsObject, boolean isApiProduct) {

        /**
         * Map additionalProperties object to array and Add additionalPropertiesMap which is new in v42
         */
        JsonObject srcAdditionalPropertyJson = apiMap.get("additionalProperties") == null ? null :
                gson.toJsonTree(apiMap.get("additionalProperties")).getAsJsonObject();
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

        apiMap.put("additionalProperties", additionalPropertyJsonArray);
        apiMap.put("additionalPropertiesMap", additionalPropertiesMap);

        /**
         * Map Business information
         */
        JsonObject businessOwnerInfoJson = gson.toJsonTree(apiMap.get("businessInformation")).getAsJsonObject();
        JsonElement businessOwner = businessOwnerInfoJson.get(Constants.API_BUSINESS_OWNER);
        if (!businessOwner.isJsonNull()) {
            businessOwnerInfoJson.addProperty(Constants.API_BUSINESS_OWNER,
                    businessOwner.getAsString().length() > 120 ? businessOwner.getAsString().substring(0, 120) :
                            businessOwner.getAsString());
        }
        JsonElement technicalOwner = businessOwnerInfoJson.get(Constants.API_TECHNICAL_OWNER);
        if (!technicalOwner.isJsonNull()) {
            businessOwnerInfoJson.addProperty(Constants.API_TECHNICAL_OWNER,
                    technicalOwner.getAsString().length() > 120 ? technicalOwner.getAsString().substring(0, 120) :
                            technicalOwner.getAsString());
        }
        apiMap.put("businessInformation", businessOwnerInfoJson);


        /**
         * add default values for newly introduced fields in 4.2.0
         */

        // common for apis and api products
        apiMap.put("isRevision", false);
        apiMap.put("revisionId", 1);
        apiMap.put("gatewayVendor", "wso2");
        apiMap.put("lastUpdatedTimestamp", new Date().getTime());
        apiMap.put("workflowStatus", null);

        if (isApiProduct) {
            apiMap.put(Constants.API_DATA_VERSION, "1.0");
            apiMap.put("isDefaultVersion", false);
            apiMap.put("revisionedApiProductId", null);

            /**
             * API products doesn't have mediation policy details in 3.2.0. Hence adding empty values
             */
            JsonArray apiArray = gson.toJsonTree(apiMap.get("apis")).getAsJsonArray();
            for (JsonElement apiElement : apiArray) {
                JsonArray apiOperations = getUpdatedOperations(apiElement.getAsJsonObject()
                        .getAsJsonArray("operations"));
                apiElement.getAsJsonObject().add("operations", apiOperations);
            }
            apiMap.put("apis", apiArray);

            /**
             * Remove the fields which are not in 4.2.0
             */
            apiMap.remove("gatewayEnvironments");

        } else {
            apiMap.put("enableSubscriberVerification", false);
            apiMap.put("audience", "PUBLIC");
            apiMap.put("gatewayType","wso2/synapse");
            apiMap.put("revisionedApiId", null);

            /**
             * Map mediation policies to API Level polices
             */
            JsonObject apiPolicyMappedObject =
                    getApiPolicyMapping(gson.toJsonTree(apiMap.get("mediationPolicies")).getAsJsonArray());
            apiMap.put("apiPolicy", apiPolicyMappedObject);

            /**
             * Add empty values for the newly introduced fields in Operations object
             */
            JsonArray operationsArray = gson.toJsonTree(apiMap.get(Constants.OPERATIONS)).getAsJsonArray();
            apiMap.put(Constants.OPERATIONS, getUpdatedOperations(operationsArray));

            //new to 4.2.0
            apiMap.put("asyncTransportProtocols", new String[]{"http", "https"});

            /**
             * Remove the fields which are not in 4.2.0
             */
            apiMap.remove("testKey");
            apiMap.remove("destinationStatsEnabled");
            apiMap.remove("enableStore");
            apiMap.remove("endpointSecurity");
            apiMap.remove("gatewayEnvironments");
            apiMap.remove("deploymentEnvironments");
            apiMap.remove("labels");
        }

        /**
         * Convert model to JsonObject
         */
        JsonObject targetJsonObject = convertModelToJsonObject(apiMap);

        /**
         * Update values received from paramJson
         */
        targetJsonObject = setFromParamsJson(paramsObject, targetJsonObject);
        return targetJsonObject;
    }

    private static JsonObject convertModelToJsonObject(Map targetModel) {
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

    /** Used in 3.2.0 to 4.2.0 mapping. Adds
     * payloadSchema : string
     * uriMapping : string
     * operationPolicies : object (API Operation Level Policies)
     */
    private static JsonArray getUpdatedOperations(JsonArray operationsArray) {
        if (operationsArray != null) {
            for (JsonElement operation : operationsArray) {
                operation.getAsJsonObject().add("payloadSchema", null);
                operation.getAsJsonObject().add("uriMapping", null);
                JsonObject operationPolicies = new JsonObject();
                operationPolicies.add("request", new JsonArray());
                operationPolicies.add("response", new JsonArray());
                operationPolicies.add("fault", new JsonArray());
                operation.getAsJsonObject().add("operationPolicies", operationPolicies);
            }
            return operationsArray;
        }
        return null;
    }

    /**
     * Map mediation policies to API Level polices
     */
    private static JsonObject getApiPolicyMapping(JsonArray mediationPolicyArray) {

        if (mediationPolicyArray != null) {
            JsonObject apiPolicyObject = new JsonObject();
            for (JsonElement mediationPolicyElement : mediationPolicyArray) {
                JsonObject mappedObject = new JsonObject();

                //required field in 3.2.0
                mappedObject.add("policyName", mediationPolicyElement.getAsJsonObject().get("name"));

                if (mediationPolicyElement.getAsJsonObject().keySet().contains("id")) {
                    mappedObject.add("policyId", mediationPolicyElement.getAsJsonObject().get("id"));
                } else {
                    mappedObject.add("policyId",null);
                }
                mappedObject.addProperty("policyVersion", "v1");
                mappedObject.add("parameters", new JsonObject());

                JsonArray policyArray = new JsonArray();
                policyArray.add(mappedObject);
                if ("in".equals(mediationPolicyElement.getAsJsonObject().get("type").getAsString())) {
                    apiPolicyObject.add("request", policyArray);
                } else if ("out".equals(mediationPolicyElement.getAsJsonObject().get("type").getAsString())) {
                    apiPolicyObject.add("response", policyArray);
                } else if ("fault".equals(mediationPolicyElement.getAsJsonObject().get("type").getAsString())) {
                    apiPolicyObject.add("fault", policyArray);
                }
            }
            return apiPolicyObject;
        }
        return null;
    }

}
