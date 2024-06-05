package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIJson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class V32APIJson extends APIJson {

    String name;
    String description;
    String context;
    String version;
    String provider;
    String lifeCycleStatus;
    Object wsdlInfo;
    boolean responseCachingEnabled;
    int cacheTimeout;
    String destinationStatsEnabled;
    boolean hasThumbnail;
    boolean isDefaultVersion;
    boolean enableSchemaValidation;
    boolean enableStore;
    String type;
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
    Object endpointSecurity;
    String[] gatewayEnvironments;
    Object deploymentEnvironments;
    String[] labels;
    Object mediationPolicies;
    String subscriptionAvailability;
    String[] subscriptionAvailableTenants;
    Object additionalProperties;
    Object monetization;
    String accessControl;
    String[] accessControlRoles;
    Object businessInformation;
    Object corsConfiguration;
    String workflowStatus;
    String createdTime;
    String lastUpdatedTime;
    Object endpointConfig;
    String endpointImplementationType;
    Object scopes;
    Object operations;
    Object threatProtectionPolicies;
    String[] categories;

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

    public String getDestinationStatsEnabled() {
        return destinationStatsEnabled;
    }

    public void setDestinationStatsEnabled(String destinationStatsEnabled) {
        this.destinationStatsEnabled = destinationStatsEnabled;
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

    public boolean isEnableSchemaValidation() {
        return enableSchemaValidation;
    }

    public void setEnableSchemaValidation(boolean enableSchemaValidation) {
        this.enableSchemaValidation = enableSchemaValidation;
    }

    public boolean isEnableStore() {
        return enableStore;
    }

    public void setEnableStore(boolean enableStore) {
        this.enableStore = enableStore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Object getEndpointSecurity() {
        return endpointSecurity;
    }

    public void setEndpointSecurity(Object endpointSecurity) {
        this.endpointSecurity = endpointSecurity;
    }

    public String[] getGatewayEnvironments() {
        return gatewayEnvironments;
    }

    public void setGatewayEnvironments(String[] gatewayEnvironments) {
        this.gatewayEnvironments = gatewayEnvironments;
    }

    public Object getDeploymentEnvironments() {
        return deploymentEnvironments;
    }

    public void setDeploymentEnvironments(Object deploymentEnvironments) {
        this.deploymentEnvironments = deploymentEnvironments;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public Object getMediationPolicies() {
        return mediationPolicies;
    }

    public void setMediationPolicies(Object mediationPolicies) {
        this.mediationPolicies = mediationPolicies;
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