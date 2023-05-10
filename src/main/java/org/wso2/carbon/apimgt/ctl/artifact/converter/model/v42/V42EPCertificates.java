package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.EPCertificates;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class V42EPCertificates extends EPCertificates {
    Map<String, String> crts = new HashMap<>();

    @Override
    public void importCertificates(String srcPath) throws CTLArtifactConversionException {

    }

    @Override
    public void exportCertificates(String targetPath, String exportFormat) throws CTLArtifactConversionException {
        if (getCertificates() != null && !getCertificates().isEmpty()) {
            String targetCertsDirectory = targetPath + File.separator + Constants.ENDPOINT_CERTIFICATES_DIRECTORY;

            CommonUtil.cleanDirectory(targetCertsDirectory);

            JsonArray certificates = new JsonArray();
            for (JsonObject certificate : getCertificates()) {
                String crtName = certificate.get("alias").getAsString() + Constants.CRT_EXTENSION;
                String certificateContent = Constants.BEGIN_CERTIFICATE_STRING.concat(System.lineSeparator())
                        .concat(crts.get(crtName)).concat(System.lineSeparator())
                        .concat(Constants.END_CERTIFICATE_STRING);
                CommonUtil.writeFile(targetCertsDirectory + File.separator + crtName, certificateContent);
                certificates.add(certificate);
            }
            //write endpoint-certificates.json/endpoint-certificates.yaml file
            String certificateFileName = targetCertsDirectory + File.separator + Constants.ENDPOINT_CERTIFICATES_CONFIG;
            ConfigFileUtil.writeV42DTOFile(certificateFileName, exportFormat, Constants.ENDPOINT_CERTIFICATES_TYPE,
                    certificates);
        }
    }

    public void setCrts(Map<String, String> crts) {
        this.crts = crts;
    }

    public Map<String, String> getCrts() {
        return crts;
    }
}
