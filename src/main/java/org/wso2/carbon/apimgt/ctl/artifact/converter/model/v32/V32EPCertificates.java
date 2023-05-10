package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.EPCertificates;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;

import java.io.File;
import java.util.List;

public class V32EPCertificates extends EPCertificates {
    @Override
    public void importCertificates(String srcPath) throws CTLArtifactConversionException {
        String pathToCerts = srcPath + File.separator + Constants.META_INFO_DIRECTORY;
        List certs = ConfigFileUtil.readV32ConfigFileToList(pathToCerts, Constants.ENDPOINT_CERTIFICATES_CONFIG);
        Gson gson = new Gson();
        for (Object cert : certs) {
            String docJson = gson.toJson(cert);
            JsonObject certificateObject = gson.fromJson(docJson, JsonObject.class);
            addCertificate(certificateObject);
        }
    }

    @Override
    public void exportCertificates(String targetPath, String exportFormat) throws CTLArtifactConversionException {
        //not implemented yet
    }
}
