package org.wso2.carbon.apimgt.ctl.artifact.converter.impl;

import org.wso2.carbon.apimgt.ctl.artifact.converter.ResourceVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.factory.ResourceFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Sequences;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.SequencesMappingUtil;

public class APISequencesConverter extends ResourceVersionConverter {
    Sequences srcSequences;
    Sequences targetSequences;

    public APISequencesConverter(String srcVersion, String targetVersion, String srcPath, String targetPath,
                                 String exportFormat) {
        super(srcVersion, targetVersion, srcPath, targetPath, exportFormat);
        this.srcSequences = ResourceFactory.getSequencesRepresentation(srcVersion);
        this.targetSequences = ResourceFactory.getSequencesRepresentation(targetVersion);
    }

    @Override
    public void convert() throws CTLArtifactConversionException {
        srcSequences.importSequences(srcPath);
        SequencesMappingUtil.mapSequences(srcSequences, targetSequences, srcVersion, targetVersion);
        targetSequences.exportSequences(srcPath, targetPath, exportFormat);
    }
}
