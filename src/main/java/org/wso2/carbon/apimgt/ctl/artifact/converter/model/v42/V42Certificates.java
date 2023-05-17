package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Certificates;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V42Certificates extends Certificates {
    Map<String, String> endpointCrts = new HashMap<>();
    Map<String, String> clientCrts = new HashMap<>();

    @Override
    public void importCertificates(String srcPath) throws CTLArtifactConversionException {

    }

    @Override
    public void exportCertificates(String targetPath, String exportFormat) throws CTLArtifactConversionException {
        exportCertificatesOfType(Constants.ENDPOINT_CERT_TYPE, targetPath, exportFormat);
        exportCertificatesOfType(Constants.CLIENT_CERTIFICATES_TYPE, targetPath, exportFormat);
    }

    private void exportCertificatesOfType(String type, String targetPath,
                                          String exportFormat) throws CTLArtifactConversionException {

        String targetCertsDirectory = null;
        String certificateFileName = null;
        String dtoType = null;
        List<JsonObject> certificates = null;
        if (Constants.CLIENT_CERT_TYPE.equals(type)) {
            targetCertsDirectory = targetPath + File.separator + Constants.CLIENT_CERTIFICATES_DIRECTORY;
            certificateFileName = targetCertsDirectory + File.separator + Constants.CLIENT_CERTIFICATES_CONFIG;
            dtoType = Constants.CLIENT_CERTIFICATES_TYPE;
            certificates = getClientCertificates();
        } else if (Constants.ENDPOINT_CERT_TYPE.equals(type)) {
            targetCertsDirectory = targetPath + File.separator + Constants.ENDPOINT_CERTIFICATES_DIRECTORY;
            certificateFileName = targetCertsDirectory + File.separator + Constants.CLIENT_CERTIFICATES_CONFIG;
            dtoType = Constants.CLIENT_CERTIFICATES_TYPE;
            certificates = getEndpointCertificates();
        }

        if (!StringUtils.isEmpty(certificateFileName) && !StringUtils.isEmpty(dtoType) &&
                !StringUtils.isEmpty(targetCertsDirectory) && certificates != null && !certificates.isEmpty()) {
            CommonUtil.cleanDirectory(targetCertsDirectory, true);

            JsonArray certs = new JsonArray();
            for (JsonObject certificate : certificates) {
                String crtName = certificate.get("alias").getAsString() + Constants.CRT_EXTENSION;
                String certificateContent = Constants.BEGIN_CERTIFICATE_STRING.concat(System.lineSeparator())
                        .concat(clientCrts.get(crtName)).concat(System.lineSeparator())
                        .concat(Constants.END_CERTIFICATE_STRING);
                CommonUtil.writeFile(targetCertsDirectory + File.separator + crtName, certificateContent);
                certs.add(certificate);
            }

            ConfigFileUtil.writeV42DTOFile(certificateFileName, exportFormat, dtoType,
                    certs);
        }

    }

    public void setEndpointCrts(Map<String, String> crts) {
        this.endpointCrts = crts;
    }

    public Map<String, String> getEndpointCrts() {
        return endpointCrts;
    }

    public void setClientCrts(Map<String, String> crts) {
        this.clientCrts = crts;
    }

    public Map<String, String> getClientCrts() {
        return clientCrts;
    }
}
