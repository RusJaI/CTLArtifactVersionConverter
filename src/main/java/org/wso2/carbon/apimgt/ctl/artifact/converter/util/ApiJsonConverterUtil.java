package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.*;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIJson;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32.V32APIJson;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42.V42APIJson;

import java.util.Set;

public class ApiJsonConverterUtil {

    //initialize gson to serialize with null values
    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    public static JsonObject convertFromV32To42(V32APIJson v32APIJson, JsonObject paramsObject,
                                                boolean isApiProduct) {
        //For APIs
        V42APIJson v42APIJson = new V42APIJson();
        v42APIJson.setName(v32APIJson.getName());
        v42APIJson.setDescription(v32APIJson.getDescription());
        v42APIJson.setContext(v32APIJson.getContext());
        v42APIJson.setVersion(v32APIJson.getVersion() == null ? "1.0" : v32APIJson.getVersion());
        v42APIJson.setProvider(v32APIJson.getProvider());
        v42APIJson.setLifeCycleStatus(v32APIJson.getLifeCycleStatus());

        //added wsdl info at the end

        v42APIJson.setResponseCachingEnabled(v32APIJson.isResponseCachingEnabled());
        v42APIJson.setCacheTimeout(v32APIJson.getCacheTimeout());
        v42APIJson.setHasThumbnail(v32APIJson.isHasThumbnail());
        v42APIJson.setDefaultVersion(v32APIJson.isDefaultVersion());

        // isRevision and revisionId added to the end
        v42APIJson.setEnableSchemaValidation(v32APIJson.isEnableSchemaValidation());

        // enableSubscriberVerification added to the end

        //type added to the end
        // audience added to the end

        v42APIJson.setTransport(v32APIJson.getTransport());
        v42APIJson.setTags(v32APIJson.getTags());
        v42APIJson.setPolicies(v32APIJson.getPolicies());
        v42APIJson.setApiThrottlingPolicy(v32APIJson.getApiThrottlingPolicy());
        v42APIJson.setAuthorizationHeader(v32APIJson.getAuthorizationHeader());
        v42APIJson.setSecurityScheme(v32APIJson.getSecurityScheme());
        v42APIJson.setMaxTps(v32APIJson.getMaxTps());
        v42APIJson.setVisibility(v32APIJson.getVisibility());
        v42APIJson.setVisibleRoles(v32APIJson.getVisibleRoles());
        v42APIJson.setVisibleTenants(v32APIJson.getVisibleTenants());

        v42APIJson.setSubscriptionAvailability(v32APIJson.getSubscriptionAvailability());
        v42APIJson.setSubscriptionAvailableTenants(v32APIJson.getSubscriptionAvailableTenants());


        //set additionalPropertiesMap which is new in v42
        JsonObject srcAdditionalPropertyJson = v32APIJson.getAdditionalProperties() == null ? null :
                gson.toJsonTree(v32APIJson.getAdditionalProperties()).getAsJsonObject();
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

        v42APIJson.setMonetization(v32APIJson.getMonetization() == null ? null :
                gson.toJsonTree(v32APIJson.getMonetization()).getAsJsonObject());
        v42APIJson.setAccessControl(v32APIJson.getAccessControl());
        v42APIJson.setAccessControlRoles(v32APIJson.getAccessControlRoles());

        JsonObject businessOwnerInfoJson = gson.toJsonTree(v32APIJson.getBusinessInformation()).getAsJsonObject();
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
        v42APIJson.setBusinessInformation(businessOwnerInfoJson);

        v42APIJson.setCorsConfiguration(gson.toJsonTree(v32APIJson.getCorsConfiguration()).getAsJsonObject());

        // added websubSubscriptionConfiguration at the end

        v42APIJson.setWorkflowStatus(v32APIJson.getWorkflowStatus());
        v42APIJson.setCreatedTime(v32APIJson.getCreatedTime());
        v42APIJson.setLastUpdatedTime(v32APIJson.getLastUpdatedTime());
        v42APIJson.setEndpointConfig(v32APIJson.getEndpointConfig() == null ? null :
                gson.toJsonTree(v32APIJson.getEndpointConfig()).getAsJsonObject());
        v42APIJson.setEndpointImplementationType(v32APIJson.getEndpointImplementationType());
        v42APIJson.setScopes(v32APIJson.getScopes());

        /**
         * map mediation policies to API Level polices
         */
        JsonArray mediationPolicyArray = v32APIJson.getMediationPolicies() == null ? null :
                gson.toJsonTree(v32APIJson.getMediationPolicies()).getAsJsonArray();
        if (mediationPolicyArray != null && !mediationPolicyArray.isEmpty()) {
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
            v42APIJson.setApiPolicies(apiPolicyObject);
        }

        v42APIJson.setThreatProtectionPolicies(v32APIJson.getThreatProtectionPolicies() == null ? null :
                gson.toJsonTree(v32APIJson.getThreatProtectionPolicies())
                .getAsJsonObject());
        v42APIJson.setCategories(v32APIJson.getCategories());

        /**
         * add default values for newly introduced fields in 4.2.0
         */

        // common for apis and api products
        v42APIJson.setRevision(false);
        v42APIJson.setRevisionId(1);
        v42APIJson.setEnableSubscriberVerification(false);
        v42APIJson.setAudience("PUBLIC");
        v42APIJson.setGatewayVendor("wso2");
        v42APIJson.setGatewayType("wso2/synapse");

        /**not added default values for
         * 1. websubSubscriptionConfiguration
         * 2. serviceInfo
         * 3. advertiseInfo
         */

        if (isApiProduct) { //only for API products
            v42APIJson.setState(v32APIJson.getState());
            v42APIJson.setApiType(v32APIJson.getApiType());
            /**
             * API products doesn't have mediation policy details in 3.2.0. Hence adding empty values
             */
            JsonArray apiArray = gson.toJsonTree(v32APIJson.getApis()).getAsJsonArray();
            for (JsonElement apiElement : apiArray) {
                JsonArray apiOperations = getUpdatedOperations(apiElement.getAsJsonObject()
                        .getAsJsonArray("operations"));
                apiElement.getAsJsonObject().add("operations", apiOperations);
            }
            v42APIJson.setApis(apiArray);

        } else { // only for APIs
            v42APIJson.setType(v32APIJson.getType());
            if (Constants.SOAP.equals(v42APIJson.getType())) {
                v42APIJson.setWsdlInfo(gson.toJsonTree(v32APIJson.getWsdlInfo()).getAsJsonObject());
            }

            /**
             * Add empty values for the newly introduced fields
             */
            JsonArray operationsArray = gson.toJsonTree(v32APIJson.getOperations()).getAsJsonArray();
            v42APIJson.setOperations(getUpdatedOperations(operationsArray));

            //new to 4.2.0
            v42APIJson.setAsyncTransportProtocols(new String[]{"http", "https"});
        }

        //convert model to JsonObject
        JsonObject targetJsonObject = convertModelToJsonObject(v42APIJson);

        //remove additional fields based on API or API Product
        /**
         * Remove the unrelated fields from an API
         */
        if (isApiProduct) {
            targetJsonObject.remove(Constants.API_ASYNC_TRANSPORT_PROTOCOLS);
            targetJsonObject.remove(Constants.API_WSDL_INFO);
            targetJsonObject.remove(Constants.API_MEDIATION_POLICIES);
            targetJsonObject.remove(Constants.API_API_POLICIES);
        } else { // an API
            targetJsonObject.remove(Constants.API_PRODUCT_STATE);
            targetJsonObject.remove(Constants.API_PRODUCT_API_TYPE);
            targetJsonObject.remove(Constants.API_PRODUCT_APIS);
            targetJsonObject.remove(Constants.API_PRODUCT_REVISIONED_ID);
        }

        //update values received from paramJson
        targetJsonObject = setFromParamsJson(paramsObject, targetJsonObject);
        return targetJsonObject;
    }

    private static JsonObject convertModelToJsonObject(APIJson targetModel) {
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

}
