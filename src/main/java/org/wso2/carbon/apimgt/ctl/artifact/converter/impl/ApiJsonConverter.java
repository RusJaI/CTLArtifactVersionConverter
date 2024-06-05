package org.wso2.carbon.apimgt.ctl.artifact.converter.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIJson;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32.V32APIJson;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ApiJsonConverterUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;

import java.io.File;
import java.io.IOException;

public class ApiJsonConverter {

    String srcVersion;
    String targetVersion;
    String srcPath;
    String targetPath;
    JsonObject paramsJson;
    ObjectMapper objectMapper;
    File sourceFile;
    APIJson sourceModel;
    boolean isApiProduct;

    public ApiJsonConverter(String srcVersion, String targetVersion, String srcPath, String targetPath,
                            JsonObject paramsJson, boolean isApiProduct) throws IOException {

        this.srcVersion = srcVersion;
        this.targetVersion = targetVersion;;
        this.srcPath = srcPath;
        this.targetPath = targetPath;
        this.paramsJson = paramsJson;
        this.isApiProduct = isApiProduct;

        objectMapper = new ObjectMapper();
        sourceFile = new File(srcPath);

    }

    public void convert() throws IOException, CTLArtifactConversionException {
        JsonObject resultingJson = null;
        if (srcVersion.equals(Constants.V320)) {
            sourceModel = objectMapper.readValue(sourceFile, V32APIJson.class);
            if (targetVersion.equals(Constants.V420)) {
                resultingJson = ApiJsonConverterUtil.convertFromV32To42((V32APIJson) sourceModel, paramsJson, isApiProduct);
            } else {
                throw new CTLArtifactConversionException("Unsupported target version");
            }
        }
        // TODO : Other version conversions

        // write to target
        CommonUtil.writeFile(targetPath, String.valueOf(resultingJson));

    }

}
