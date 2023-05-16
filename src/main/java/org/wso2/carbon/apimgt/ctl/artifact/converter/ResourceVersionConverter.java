package org.wso2.carbon.apimgt.ctl.artifact.converter;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

public abstract class ResourceVersionConverter {
    protected String srcVersion;
    protected String targetVersion;
    protected String srcPath;
    protected String targetPath;
    protected String exportFormat;

    public ResourceVersionConverter(String srcVersion, String targetVersion, String srcPath, String targetPath,
                                    String exportFormat) {
        this.srcVersion = srcVersion;
        this.targetVersion = targetVersion;
        this.srcPath = srcPath;
        this.targetPath = targetPath;
        this.exportFormat = exportFormat;
    }
    public abstract void convert() throws CTLArtifactConversionException;
}
