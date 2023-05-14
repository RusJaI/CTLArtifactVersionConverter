package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32;

import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Sequences;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.SequencesMappingUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V32Sequences extends Sequences {
    private static final Log log = LogFactory.getLog(V32Sequences.class);
    protected Map<String, List<JsonObject>> sequencesMap = new HashMap<>();
    @Override
    public void importSequences(String srcPath) throws CTLArtifactConversionException {
        String pathToSequences = srcPath + File.separator + Constants.SEQUENCES_DIRECTORY;
        File srcSequencesDirectory = new File(pathToSequences);

        if (srcSequencesDirectory.exists()) {
            List<JsonObject> inSequences = SequencesMappingUtil.readV32Sequences(pathToSequences, Constants.IN);
            List<JsonObject> outSequences = SequencesMappingUtil.readV32Sequences(pathToSequences, Constants.OUT);
            List<JsonObject> faultSequences = SequencesMappingUtil.readV32Sequences(pathToSequences, Constants.FAULT);

            sequencesMap.put(Constants.IN, inSequences);
            sequencesMap.put(Constants.OUT, outSequences);
            sequencesMap.put(Constants.FAULT, faultSequences);
        } else {
            log.info("No sequences found in the src path " + pathToSequences);
        }
    }

    @Override
    public void exportSequences(String srcPath, String targetPath, String exportFormat) throws CTLArtifactConversionException {
        //not implemented
    }

    public Map<String, List<JsonObject>> getSequencesMap() {
        return sequencesMap;
    }

    public void setSequencesMap(Map<String, List<JsonObject>> sequencesMap) {
        this.sequencesMap = sequencesMap;
    }

    public void addSequence(JsonObject sequence, String flowType) {
        sequencesMap.get(flowType).add(sequence);
    }

    public List<JsonObject> getSequences(String flowType) {
        return sequencesMap.get(flowType);
    }
}
