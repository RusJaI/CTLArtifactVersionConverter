package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Certificates;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;

import java.io.File;
import java.util.List;

public class V32Certificates extends Certificates {
    @Override
    public void importCertificates(String srcPath) throws CTLArtifactConversionException {
        importCertificatesOfType(srcPath, Constants.ENDPOINT_CERT_TYPE);
        importCertificatesOfType(srcPath, Constants.CLIENT_CERT_TYPE);
    }

    @Override
    public void exportCertificates(String targetPath, String exportFormat) throws CTLArtifactConversionException {
        //not implemented yet
    }

    private void importCertificatesOfType(String srcPath, String type) throws CTLArtifactConversionException {
        String pathToCerts = srcPath + File.separator + Constants.META_INFO_DIRECTORY;
        Gson gson = new Gson();
        List certs = null;
        if (Constants.ENDPOINT_CERT_TYPE.equals(type)) {
            certs = ConfigFileUtil.readV32ConfigFileToList(pathToCerts, Constants.ENDPOINT_CERTIFICATES_CONFIG);
        } else if (Constants.CLIENT_CERT_TYPE.equals(type)) {
            certs = ConfigFileUtil.readV32ConfigFileToList(pathToCerts, Constants.CLIENT_CERTIFICATES_CONFIG);
        }

        if (certs != null && !certs.isEmpty()) {
            for (Object cert : certs) {
                String certJson = gson.toJson(cert);
                JsonObject certificateObject = gson.fromJson(certJson, JsonObject.class);
                addCertificate(certificateObject, type);
            }
        }
    }
}
