package org.wso2.carbon.apimgt.ctl.artifact.converter.impl;

import org.wso2.carbon.apimgt.ctl.artifact.converter.ResourceVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.factory.ResourceFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.APIInfo;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.APIInfoMappingUtil;

public class APIInfoVersionConverter extends ResourceVersionConverter {
    APIInfo srcAPIInfo;
    APIInfo targetAPIInfo;

    public APIInfoVersionConverter(String srcVersion, String targetVersion, String srcPath, String targetPath,
                                   String exportFormat) {
        super(srcVersion, targetVersion, srcPath, targetPath, exportFormat);
        this.srcAPIInfo = ResourceFactory.getAPIInfoRepresentation(srcVersion);
        this.targetAPIInfo = ResourceFactory.getAPIInfoRepresentation(targetVersion);
    }
    @Override
    public void convert() throws CTLArtifactConversionException {
        //Import API info from src artifact
        srcAPIInfo.importAPIInfo(srcPath);

        //Map imported API info to target API Info format
        APIInfoMappingUtil.mapAPIInfo(srcAPIInfo, targetAPIInfo, srcVersion, targetVersion);

        //Export mapped certificates to target artifact
        targetAPIInfo.exportAPIInfo(srcPath, targetPath, exportFormat);
    }
}
