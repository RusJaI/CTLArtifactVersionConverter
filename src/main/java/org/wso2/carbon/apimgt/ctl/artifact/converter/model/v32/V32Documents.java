package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v32;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Documents;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;

import java.io.*;
import java.util.List;

public class V32Documents extends Documents {
    private static final Log log = LogFactory.getLog(V32Documents.class);
    @Override
    public void importDocuments(String srcPath) throws CTLArtifactConversionException {
        String pathToDocs = srcPath + File.separator + Constants.DOCS_DIRECTORY;
        File srcDocumentsDirectory = new File(pathToDocs);

        if (srcDocumentsDirectory.exists()) {
            List docs = ConfigFileUtil.readV32ConfigFileToList(pathToDocs, Constants.DOCS_CONFIG_FILE);
            Gson gson = new Gson();
            for (Object doc : docs) {
                String docJson = gson.toJson(doc);
                JsonObject docObject = gson.fromJson(docJson, JsonObject.class);
                addDocument(docObject);
            }
        } else {
            log.info("No documents found in the src path " + pathToDocs);
        }
    }

    @Override
    public void exportDocuments(String targetPath, String srcPath, String exportFormat) throws CTLArtifactConversionException {
        //not implemented yet
    }

}
