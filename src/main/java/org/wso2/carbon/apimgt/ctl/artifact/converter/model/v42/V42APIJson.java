package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42;

import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIJson;

public class V42APIJson extends APIJson {

    String name;
    String description;
    String context;
    String version;;
    String provider;
    String lifeCycleStatus;
    Object wsdlInfo;
    boolean responseCachingEnabled;
    int cacheTimeout;
    boolean hasThumbnail;
    boolean isDefaultVersion;
    boolean isRevision;
    int revisionId;
    int revisionedApiProductId;
    boolean enableSchemaValidation;
    boolean enableSubscriberVerification;
    String type;
    String audience;
    String[] transport;
    String[] tags;
    String[] policies;
    String apiThrottlingPolicy;
    String authorizationHeader;
    String[] securityScheme;
    Object maxTps;
    String visibility;
    String[] visibleRoles;
    String[] visibleTenants;
    Object mediationPolicies;
    Object apiPolicies;
    String subscriptionAvailability;
    String[] subscriptionAvailableTenants;
    Object additionalProperties;
    Object additionalPropertiesMap;
    Object monetization;
    String accessControl;
    String[] accessControlRoles;
    Object businessInformation;
    Object corsConfiguration;
    Object websubSubscriptionConfiguration;
    String workflowStatus;
    String createdTime;
    String lastUpdatedTime;
    String lastUpdatedTimestamp;
    Object endpointConfig;
    String endpointImplementationType;
    Object scopes;
    Object operations;
    Object threatProtectionPolicies;
    String[] categories;
    Object serviceInfo;
    Object advertiseInfo;
    String gatewayVendor;
    String gatewayType;
    String[] asyncTransportProtocols;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    public void setLifeCycleStatus(String lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
    }

    public Object getWsdlInfo() {
        return wsdlInfo;
    }

    public void setWsdlInfo(Object wsdlInfo) {
        this.wsdlInfo = wsdlInfo;
    }

    public boolean isResponseCachingEnabled() {
        return responseCachingEnabled;
    }

    public void setResponseCachingEnabled(boolean responseCachingEnabled) {
        this.responseCachingEnabled = responseCachingEnabled;
    }

    public int getCacheTimeout() {
        return cacheTimeout;
    }

    public void setCacheTimeout(int cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    public boolean isHasThumbnail() {
        return hasThumbnail;
    }

    public void setHasThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public boolean isDefaultVersion() {
        return isDefaultVersion;
    }

    public void setDefaultVersion(boolean defaultVersion) {
        isDefaultVersion = defaultVersion;
    }

    public boolean isRevision() {
        return isRevision;
    }

    public void setRevision(boolean revision) {
        isRevision = revision;
    }

    public int getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(int revisionId) {
        this.revisionId = revisionId;
    }

    public int getRevisionedApiProductId() {
        return revisionedApiProductId;
    }

    public void setRevisionedApiProductId(int revisionedApiProductId) {
        this.revisionedApiProductId = revisionedApiProductId;
    }

    public boolean isEnableSchemaValidation() {
        return enableSchemaValidation;
    }

    public void setEnableSchemaValidation(boolean enableSchemaValidation) {
        this.enableSchemaValidation = enableSchemaValidation;
    }

    public boolean isEnableSubscriberVerification() {
        return enableSubscriberVerification;
    }

    public void setEnableSubscriberVerification(boolean enableSubscriberVerification) {
        this.enableSubscriberVerification = enableSubscriberVerification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String[] getTransport() {
        return transport;
    }

    public void setTransport(String[] transport) {
        this.transport = transport;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getPolicies() {
        return policies;
    }

    public void setPolicies(String[] policies) {
        this.policies = policies;
    }

    public String getApiThrottlingPolicy() {
        return apiThrottlingPolicy;
    }

    public void setApiThrottlingPolicy(String apiThrottlingPolicy) {
        this.apiThrottlingPolicy = apiThrottlingPolicy;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public String[] getSecurityScheme() {
        return securityScheme;
    }

    public void setSecurityScheme(String[] securityScheme) {
        this.securityScheme = securityScheme;
    }

    public Object getMaxTps() {
        return maxTps;
    }

    public void setMaxTps(Object maxTps) {
        this.maxTps = maxTps;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String[] getVisibleRoles() {
        return visibleRoles;
    }

    public void setVisibleRoles(String[] visibleRoles) {
        this.visibleRoles = visibleRoles;
    }

    public String[] getVisibleTenants() {
        return visibleTenants;
    }

    public void setVisibleTenants(String[] visibleTenants) {
        this.visibleTenants = visibleTenants;
    }

    public Object getMediationPolicies() {
        return mediationPolicies;
    }

    public void setMediationPolicies(Object mediationPolicies) {
        this.mediationPolicies = mediationPolicies;
    }

    public Object getApiPolicies() {
        return apiPolicies;
    }

    public void setApiPolicies(Object apiPolicies) {
        this.apiPolicies = apiPolicies;
    }

    public String getSubscriptionAvailability() {
        return subscriptionAvailability;
    }

    public void setSubscriptionAvailability(String subscriptionAvailability) {
        this.subscriptionAvailability = subscriptionAvailability;
    }

    public String[] getSubscriptionAvailableTenants() {
        return subscriptionAvailableTenants;
    }

    public void setSubscriptionAvailableTenants(String[] subscriptionAvailableTenants) {
        this.subscriptionAvailableTenants = subscriptionAvailableTenants;
    }

    public Object getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Object additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public Object getAdditionalPropertiesMap() {
        return additionalPropertiesMap;
    }

    public void setAdditionalPropertiesMap(Object additionalPropertiesMap) {
        this.additionalPropertiesMap = additionalPropertiesMap;
    }

    public Object getMonetization() {
        return monetization;
    }

    public void setMonetization(Object monetization) {
        this.monetization = monetization;
    }

    public String getAccessControl() {
        return accessControl;
    }

    public void setAccessControl(String accessControl) {
        this.accessControl = accessControl;
    }

    public String[] getAccessControlRoles() {
        return accessControlRoles;
    }

    public void setAccessControlRoles(String[] accessControlRoles) {
        this.accessControlRoles = accessControlRoles;
    }

    public Object getBusinessInformation() {
        return businessInformation;
    }

    public void setBusinessInformation(Object businessInformation) {
        this.businessInformation = businessInformation;
    }

    public Object getCorsConfiguration() {
        return corsConfiguration;
    }

    public void setCorsConfiguration(Object corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }

    public Object getWebsubSubscriptionConfiguration() {
        return websubSubscriptionConfiguration;
    }

    public void setWebsubSubscriptionConfiguration(Object websubSubscriptionConfiguration) {
        this.websubSubscriptionConfiguration = websubSubscriptionConfiguration;
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Object getEndpointConfig() {
        return endpointConfig;
    }

    public void setEndpointConfig(Object endpointConfig) {
        this.endpointConfig = endpointConfig;
    }

    public String getEndpointImplementationType() {
        return endpointImplementationType;
    }

    public void setEndpointImplementationType(String endpointImplementationType) {
        this.endpointImplementationType = endpointImplementationType;
    }

    public Object getScopes() {
        return scopes;
    }

    public void setScopes(Object scopes) {
        this.scopes = scopes;
    }

    public Object getOperations() {
        return operations;
    }

    public void setOperations(Object operations) {
        this.operations = operations;
    }

    public Object getThreatProtectionPolicies() {
        return threatProtectionPolicies;
    }

    public void setThreatProtectionPolicies(Object threatProtectionPolicies) {
        this.threatProtectionPolicies = threatProtectionPolicies;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public Object getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(Object serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public Object getAdvertiseInfo() {
        return advertiseInfo;
    }

    public void setAdvertiseInfo(Object advertiseInfo) {
        this.advertiseInfo = advertiseInfo;
    }

    public String getGatewayVendor() {
        return gatewayVendor;
    }

    public void setGatewayVendor(String gatewayVendor) {
        this.gatewayVendor = gatewayVendor;
    }

    public String getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    public String[] getAsyncTransportProtocols() {
        return asyncTransportProtocols;
    }

    public void setAsyncTransportProtocols(String[] asyncTransportProtocols) {
        this.asyncTransportProtocols = asyncTransportProtocols;
    }

    /**
     * Following are for api products
     */

    String state;
    String apiType;
    Object apis;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public Object getApis() {
        return apis;
    }

    public void setApis(Object apis) {
        this.apis = apis;
    }
}
