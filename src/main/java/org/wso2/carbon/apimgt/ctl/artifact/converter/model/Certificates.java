package org.wso2.carbon.apimgt.ctl.artifact.converter.model;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

import java.util.ArrayList;
import java.util.List;

public abstract class Certificates {
    private List<JsonObject> endpointCertificates = new ArrayList<>();
    public List<JsonObject> clientCertificates = new ArrayList<>();

    public abstract void importCertificates(String srcPath) throws CTLArtifactConversionException;

    public abstract void exportCertificates(String targetPath, String exportFormat) throws CTLArtifactConversionException;

    public void addEndpointCertificate(JsonObject certificate) {
        endpointCertificates.add(certificate);
    }

    public void removeEndpointCertificate(JsonObject certificate) {
        endpointCertificates.remove(certificate);
    }

    public List<JsonObject> getEndpointCertificates() {
        return endpointCertificates;
    }

    public void setEndpointCertificates(List certificates) {
        this.endpointCertificates = certificates;
    }

    public void addClientCertificate(JsonObject certificate) {
        clientCertificates.add(certificate);
    }

    public void removeClientCertificate(JsonObject certificate) {
        clientCertificates.remove(certificate);
    }

    public List<JsonObject> getClientCertificates() {
        return clientCertificates;
    }

    public void setClientCertificates(List certificates) {
        this.clientCertificates = certificates;
    }

    public void addCertificate(JsonObject certificate, String type) {
        if (Constants.ENDPOINT_CERT_TYPE.equals(type)) {
            addEndpointCertificate(certificate);
        } else if (Constants.CLIENT_CERT_TYPE.equals(type)) {
            addClientCertificate(certificate);
        }
    }
}
