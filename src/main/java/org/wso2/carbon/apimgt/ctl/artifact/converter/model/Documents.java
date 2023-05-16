package org.wso2.carbon.apimgt.ctl.artifact.converter.model;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

import java.util.ArrayList;
import java.util.List;

public abstract class Documents {
    private List<JsonObject> documents = new ArrayList<>();

    public abstract void importDocuments(String srcPath) throws CTLArtifactConversionException;

    public abstract void exportDocuments(String targetPath, String srcPath, String exportFormat)
            throws CTLArtifactConversionException;

    public void addDocument(JsonObject document) {
        documents.add(document);
    }

    public void removeDocument(JsonObject document) {
        documents.remove(document);
    }

    public List<JsonObject> getDocuments() {
        return documents;
    }

    public void setDocuments(List documents) {
        this.documents = documents;
    }
}
