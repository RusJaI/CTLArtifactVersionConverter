package org.wso2.carbon.apimgt.ctl.artifact.converter.model;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Sequences {

    public abstract void importSequences(String srcPath) throws CTLArtifactConversionException;

    public abstract void exportSequences(String srcPath, String targetPath, String exportFormat) throws CTLArtifactConversionException;
}
