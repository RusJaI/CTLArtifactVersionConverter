package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;

import java.util.ArrayList;
import java.util.List;

public class DocumentsMappingUtil {
    public static List<JsonObject> mapDocuments(List<JsonObject> documents, String srcVersion, String targetVersion) {
        if (srcVersion.equals(Constants.V320) && targetVersion.equals(Constants.V420)) {
            return v32tov42Documents(documents);
        }
        //todo: implement other versions, if no matching conversion found(unlikely to happen), return the same list
        return documents;
    }


    public static List<JsonObject> v32tov42Documents(List<JsonObject> v32documents) {
        List<JsonObject> v42documents = new ArrayList<>();
        for (JsonObject v32document : v32documents) {
            JsonObject v42Document = new JsonObject();
            v42Document.addProperty("documentId", v32document.get("id").getAsString());
            v42Document.addProperty("type", v32document.get("type").getAsString());
            v42Document.addProperty("name", v32document.get("name").getAsString());
            v42Document.addProperty("summary", v32document.get("summary").getAsString());
            v42Document.addProperty("sourceType", v32document.get("sourceType").getAsString());
            v42Document.addProperty("visibility", v32document.get("visibility").getAsString());

            if (v32document.get("sourceType").getAsString().equals("FILE")) {
                String filePath = v32document.get("filePath") != null ? v32document.get("filePath").getAsString() : null;
                if (filePath != null) {
                    v42Document.addProperty("fileName", v32document.get("filePath").getAsString());
                }
            } else if (v32document.get("sourceType").getAsString().equals("URL")) {
                v42Document.addProperty("sourceUrl", v32document.get("sourceUrl").getAsString());
            }
            v42documents.add(v42Document);
        }
        return v42documents;
    }
}
