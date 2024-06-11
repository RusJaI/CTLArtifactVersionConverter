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

        apiMap.put(Constants.AJ_ADDITIONAL_PROPERTIES, additionalPropertyJsonArray);
        apiMap.put(Constants.AJ_ADDITIONAL_PROPERTIES_MAP, additionalPropertiesMap);

        /**
         * Map Business information
         */
        JsonObject businessOwnerInfoJson = gson.toJsonTree(apiMap.get(Constants.AJ_BUSINESS_INFORMATION)).getAsJsonObject();
        JsonElement businessOwner = businessOwnerInfoJson.get(Constants.AJ_API_BUSINESS_OWNER);
        if (!businessOwner.isJsonNull()) {
            businessOwnerInfoJson.addProperty(Constants.AJ_API_BUSINESS_OWNER,
                    businessOwner.getAsString().length() > 120 ? businessOwner.getAsString().substring(0, 120) :
                            businessOwner.getAsString());
        }
        JsonElement technicalOwner = businessOwnerInfoJson.get(Constants.AJ_API_TECHNICAL_OWNER);
        if (!technicalOwner.isJsonNull()) {
            businessOwnerInfoJson.addProperty(Constants.AJ_API_TECHNICAL_OWNER,
                    technicalOwner.getAsString().length() > 120 ? technicalOwner.getAsString().substring(0, 120) :
                            technicalOwner.getAsString());
        }
        apiMap.put(Constants.AJ_BUSINESS_INFORMATION, businessOwnerInfoJson);


        /**
         * add default values for newly introduced fields in 4.2.0
         */

        // common for apis and api products
        apiMap.put(Constants.AJ_IS_REVISION, false);
        apiMap.put(Constants.AJ_REVISION_ID, 1);
        apiMap.put(Constants.AJ_GATEWAY_VENDOR, Constants.GATEWAY_VENDOR_WSO2);
        apiMap.put(Constants.AJ_LAST_UPDATED_TIMESTAMP, new Date().getTime());
        apiMap.put(Constants.AJ_WORKFLOW_STATUS, null);

        if (isApiProduct) {
            apiMap.put(Constants.API_DATA_VERSION, "1.0");
            apiMap.put(Constants.AJ_IS_DEFAULT_VERSION, false);
            apiMap.put(Constants.AJ_REVISIONED_API_PRODUCT_ID, null);

            /**
             * API products doesn't have mediation policy details in 3.2.0. Hence adding empty values
             */
            JsonArray apiArray = gson.toJsonTree(apiMap.get(Constants.AJ_APIS)).getAsJsonArray();
            for (JsonElement apiElement : apiArray) {
                JsonArray apiOperations = getUpdatedOperations(apiElement.getAsJsonObject()
                        .getAsJsonArray(Constants.AJ_OPERATIONS));
                apiElement.getAsJsonObject().add(Constants.AJ_OPERATIONS, apiOperations);
            }
            apiMap.put(Constants.AJ_APIS, apiArray);

            /**
             * Remove the fields which are not in 4.2.0
             */
            apiMap.remove(Constants.AJ_GATEWAY_ENVIRONMENTS);

        } else {
            apiMap.put(Constants.AJ_ENABLE_SUBSCRIBER_VERIFICATION, false);
            apiMap.put(Constants.AJ_AUDIENCE, "PUBLIC");
            apiMap.put(Constants.AJ_GATEWAY_TYPE, Constants.GATEWAY_TYPE_WSO2_SYNAPSE);
            apiMap.put(Constants.AJ_REVISIONED_API_ID, null);

            /**
             * Map mediation policies to API Level polices
             */
            JsonObject apiPolicyMappedObject = getApiPolicyMapping(gson.toJsonTree(apiMap
                    .get(Constants.AJ_MEDIATION_POLICIES)).getAsJsonArray());
            apiMap.put(Constants.AJ_API_POLICIES, apiPolicyMappedObject);

            /**
             * Add empty values for the newly introduced fields in Operations object
             */
            JsonArray operationsArray = gson.toJsonTree(apiMap.get(Constants.AJ_OPERATIONS)).getAsJsonArray();
            apiMap.put(Constants.AJ_OPERATIONS, getUpdatedOperations(operationsArray));

            //new to 4.2.0
            apiMap.put(Constants.AJ_ASYNC_TRANSPORT_PROTOCOLS, new String[]{"http", "https"});
            //replace mediation policy object with an empty array
            apiMap.put(Constants.AJ_MEDIATION_POLICIES, new JsonArray());

            /**
             * Remove the fields which are not in 4.2.0
             */
            apiMap.remove(Constants.AJ_TEST_KEY);
            apiMap.remove(Constants.AJ_DESTINATION_STATS_ENABLED);
            apiMap.remove(Constants.AJ_ENABLE_STORE);
            apiMap.remove(Constants.AJ_ENDPOINT_SECURITY);
            apiMap.remove(Constants.AJ_GATEWAY_ENVIRONMENTS);
            apiMap.remove(Constants.AJ_DEPLOYMENT_ENVIRONMENTS);
            apiMap.remove(Constants.AJ_LABELS);
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
                operationPolicies.add(Constants.AJ_POLICY_TYPE_REQUEST, new JsonArray());
                operationPolicies.add(Constants.AJ_POLICY_TYPE_RESPONSE, new JsonArray());
                operationPolicies.add(Constants.AJ_POLICY_TYPE_FAULT, new JsonArray());
                operation.getAsJsonObject().add(Constants.AJ_OPERATION_POLICIES, operationPolicies);
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
                if ("in".equalsIgnoreCase(mediationPolicyElement.getAsJsonObject()
                        .get(Constants.AJ_POLICY_TYPE).getAsString())) {
                    apiPolicyObject.add(Constants.AJ_POLICY_TYPE_REQUEST, policyArray);
                } else if ("out".equalsIgnoreCase(mediationPolicyElement.getAsJsonObject()
                        .get(Constants.AJ_POLICY_TYPE).getAsString())) {
                    apiPolicyObject.add(Constants.AJ_POLICY_TYPE_RESPONSE, policyArray);
                } else if ("fault".equalsIgnoreCase(mediationPolicyElement.getAsJsonObject()
                        .get(Constants.AJ_POLICY_TYPE).getAsString())) {
                    apiPolicyObject.add(Constants.AJ_POLICY_TYPE_FAULT, policyArray);
                }
            }
            return apiPolicyObject;
        }
        return null;
    }

}
