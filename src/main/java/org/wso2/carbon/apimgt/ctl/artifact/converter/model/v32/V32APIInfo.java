package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIInfo;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;

import java.io.File;
import java.util.Map;

public class V32APIInfo extends APIInfo {
    private static final Log log = LogFactory.getLog(V32Certificates.class);
    @Override
    public void importAPIInfo(String srcPath) throws CTLArtifactConversionException {
        String pathToMetaInfo = srcPath + File.separator + Constants.META_INFO_DIRECTORY;
        File srcMetaInfoDirectory = new File(pathToMetaInfo);

        if (srcMetaInfoDirectory.exists()) {
            Map apiMap = ConfigFileUtil.readConfigFileToMap(pathToMetaInfo, Constants.API_CONFIG);

            Gson gson = new Gson();
            JsonObject apiJsonObject = gson.fromJson(gson.toJson(apiMap), JsonObject.class);
            setApiInfo(apiJsonObject);
        } else {
            log.info("Meta information directory does not exist in the artifact location. " +
                    "Hence API information is not imported.");
        }
    }

    @Override
    public void exportAPIInfo(String srcPath, String targetPath, Boolean isAPIProduct, String exportFormat) throws CTLArtifactConversionException {
        //not implemented yet
    }
}
