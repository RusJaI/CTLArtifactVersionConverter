package org.wso2.carbon.apimgt.ctl.artifact.converter;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.APIInfoVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.APISequencesConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.DocumentVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.CertificateVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class ArtifactConversionManager {
    public List<ResourceVersionConverter> converters = new ArrayList<>();
    private String srcVersion;
    private String targetVersion;
    private String srcPath;
    private String targetPath;
    private String format;


    public ArtifactConversionManager(String srcVersion, String targetVersion, String srcPath, String targetPath,
                                     String format) throws CTLArtifactConversionException {
        this.srcVersion = srcVersion;
        this.targetVersion = targetVersion;
        this.srcPath = srcPath;
        this.targetPath = targetPath;
        this.format = format;
        init();
    }

    public void init() throws CTLArtifactConversionException {
        if (CommonUtil.validateSrcAndTargetVersions(srcVersion, targetVersion)) {
            converters.add(new DocumentVersionConverter(srcVersion, targetVersion, srcPath, targetPath, format));
            converters.add(new CertificateVersionConverter(srcVersion, targetVersion, srcPath, targetPath, format));
            converters.add(new APIInfoVersionConverter(srcVersion, targetVersion, srcPath, targetPath, format));
            converters.add(new APISequencesConverter(srcVersion, targetVersion, srcPath, targetPath, format));
        } else {
            String msg = "Invalid source or target version";
            throw new CTLArtifactConversionException(msg);
        }

    }
    public boolean convert() throws CTLArtifactConversionException {
        for (ResourceVersionConverter converter : converters) {
            converter.convert();
        }
        return true;
    }
}
