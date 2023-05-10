package org.wso2.carbon.apimgt.ctl.artifact.converter.model;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

import java.util.ArrayList;
import java.util.List;

public abstract class EPCertificates {
    private List<JsonObject> certificates = new ArrayList<>();

    public abstract void importCertificates(String srcPath) throws CTLArtifactConversionException;

    public abstract void exportCertificates(String targetPath, String exportFormat) throws CTLArtifactConversionException;

    public void addCertificate(JsonObject certificate) {
        certificates.add(certificate);
    }

    public void removeCertificate(JsonObject certificate) {
        certificates.remove(certificate);
    }

    public List<JsonObject> getCertificates() {
        return certificates;
    }

    public void setCertificates(List certificates) {
        this.certificates = certificates;
    }
}
