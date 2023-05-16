package org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Documents;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.ConfigFileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class V42Documents extends Documents {
    @Override
    public void importDocuments(String srcPath) {
        //not implemented yet
    }

    /**
     * Export documents
     * @param targetPath    target directory path
     * @param srcPath       source directory path
     * @param exportFormat  export format
     * @throws CTLArtifactConversionException   if an error occurs while exporting the documents
     */
    @Override
    public void exportDocuments(String targetPath, String srcPath, String exportFormat) throws
            CTLArtifactConversionException {
        if (getDocuments() != null && !getDocuments().isEmpty()) {
            String targetDocsDirectory = targetPath + File.separator + Constants.DOCS_DIRECTORY;
            CommonUtil.cleanDirectory(targetDocsDirectory);

            String srcDocsDirectory = srcPath + File.separator + Constants.DOCS_DIRECTORY;
            for (JsonObject document : getDocuments()) {
                exportDocument(document, srcDocsDirectory, targetDocsDirectory, exportFormat);
            }
        }
    }

    /**
     * Export a single document
     *
     * @param document      document to be exported
     * @param srcPath       source directory path
     * @param targetPath    target directory path
     * @param exportFormat  export format
     * @throws CTLArtifactConversionException if an error occurs while exporting the document
     */
    private void exportDocument(JsonObject document, String srcPath, String targetPath, String exportFormat) throws
            CTLArtifactConversionException {
        try {
            String artifactName = document.get("name").getAsString();
            String docDirectory = targetPath + File.separator + artifactName;
            Files.createDirectories(Paths.get(docDirectory));

            String docFilePath = docDirectory + File.separator + Constants.DOCUMENT_TYPE;
            ConfigFileUtil.writeV42DTOFile(docFilePath, exportFormat, Constants.DOCUMENT_TYPE, document);
            ConfigFileUtil.writeV42DocumentContentFile(srcPath, targetPath, document);
        } catch (IOException e) {
            String msg = "Error while exporting document " + document.get("name").getAsString() + " to " + targetPath +
                    File.separator + Constants.DOCS_DIRECTORY;
            throw new CTLArtifactConversionException(msg, e);
        }
    }
}
