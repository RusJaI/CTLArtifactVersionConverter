package org.wso2.carbon.apimgt.ctl.artifact.converter;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.APIInfoVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.CertificateVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.DocumentVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class APIProductArtifactConversionManager {
    private String srcVersion;
    private String targetVersion;
    private String srcPath;
    private String targetPath;
    private String format;
    public List<ResourceVersionConverter> converters = new ArrayList<>();
    private List<String> dependentAPIs = new ArrayList<>();

    public APIProductArtifactConversionManager(String srcVersion, String targetVersion, String srcPath, String targetPath,
                                        String format) throws CTLArtifactConversionException {
        this.srcVersion = srcVersion;
        this.targetVersion = targetVersion;
        this.srcPath = srcPath;
        this.targetPath = targetPath;
        this.format = format;
        init();
    }

    private void init() throws CTLArtifactConversionException {
        if (CommonUtil.validateSrcAndTargetVersions(srcVersion, targetVersion)) {
            converters.add(new DocumentVersionConverter(srcVersion, targetVersion, srcPath, targetPath, format));
            //API Product can have client certificates
            converters.add(new CertificateVersionConverter(srcVersion, targetVersion, srcPath, targetPath, format));
            converters.add(new APIInfoVersionConverter(srcVersion, targetVersion, srcPath, targetPath, true, format));

            String pathToAPIs = srcPath + File.separator + Constants.APIS_DIRECTORY;
            File apisFolder = new File(pathToAPIs);
            File[] listOfAPIs = apisFolder.listFiles();
            if (listOfAPIs != null) {
                for (File api : listOfAPIs) {
                    if (api.isDirectory()) {
                        String apiName = api.getName();
                        dependentAPIs.add(apiName);
                    }
                }
            }
        } else {
            String msg = "Invalid source or target version";
            throw new CTLArtifactConversionException(msg);
        }
    }

    public boolean convert() throws CTLArtifactConversionException {
        for (ResourceVersionConverter converter : converters) {
            converter.convert();
        }

        APIArtifactConversionManager apiArtifactConversionManager;
        for (String dependentAPI : dependentAPIs) {
            String srcAPIPath = srcPath + File.separator + Constants.APIS_DIRECTORY + File.separator + dependentAPI;
            String targetAPIPath = targetPath + File.separator + Constants.APIS_DIRECTORY + File.separator + dependentAPI;
            apiArtifactConversionManager = new APIArtifactConversionManager(
                    srcVersion, targetVersion, srcAPIPath, targetAPIPath, format);
            apiArtifactConversionManager.convert();
        }
        return true;
    }
}
