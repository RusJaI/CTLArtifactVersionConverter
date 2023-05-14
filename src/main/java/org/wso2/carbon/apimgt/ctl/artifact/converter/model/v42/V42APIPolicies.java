package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Sequences;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.SequencesMappingUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class V42APIPolicies extends Sequences {
    protected Map<String, JsonObject> sequencesMap = new HashMap<>();
    @Override
    public void importSequences(String srcPath) throws CTLArtifactConversionException {
        String msg = "Method not implemented yet";
        throw new CTLArtifactConversionException(msg);
    }

    @Override
    public void exportSequences(String srcPath, String targetPath, String exportFormat) throws CTLArtifactConversionException {
        String targetPoliciesDirectory = targetPath + File.separator + Constants.POLICIES_DIRECTORY;

        if (sequencesMap.size() > 0) {
            CommonUtil.cleanDirectory(targetPoliciesDirectory);
            for (Map.Entry<String, JsonObject> entry : sequencesMap.entrySet()) {
                String policyName = entry.getKey();
                JsonObject policy = entry.getValue();
                String policyVersion = policy.get("version").getAsString();
                String policyFileName = policyName + "_" + policyVersion;

                String policySpec = policy.get("policySpec").getAsString();
                policy.remove("policySpec");

                String policyFilePath = targetPoliciesDirectory + File.separator + policyFileName;
                ConfigFileUtil.writeV42DTOFile(policyFilePath, exportFormat, Constants.POLICY_SPEC_TYPE, policy);

                //write policy j2 file
                String policyJ2FilePath = targetPoliciesDirectory + File.separator + policyFileName + ".j2";
                try (OutputStream out = new FileOutputStream(policyJ2FilePath)) {
                    out.write(policySpec.getBytes());
                } catch (IOException e) {
                    String msg = "Error while writing policy j2 file at: " + policyJ2FilePath;
                }
            }
        }

        String srcMetaInfoDirectory = srcPath + File.separator + Constants.META_INFO_DIRECTORY;
        JsonObject v32apiMap = CommonUtil.convertMapToJsonObject(ConfigFileUtil.readConfigFileToMap(srcMetaInfoDirectory, Constants.API_CONFIG));
        JsonObject v42apiMap = CommonUtil.convertMapToJsonObject(ConfigFileUtil.readConfigFileToMap(targetPath, Constants.API_CONFIG));

        SequencesMappingUtil.populateAPIPoliciesInAPI(v32apiMap, v42apiMap, sequencesMap);
        ConfigFileUtil.writeV42APIConfigFile(targetPath, exportFormat, v42apiMap, false);
    }

    public void addAPIPolicy(JsonObject sequence) {
        String key = sequence.get("name").getAsString();
        sequencesMap.put(key, sequence);
    }

    public JsonObject getAPIPolicy(String key) {
        if (sequencesMap.get(key) != null) {
            return sequencesMap.get(key);
        } else {
            return null;
        }
    }

}
