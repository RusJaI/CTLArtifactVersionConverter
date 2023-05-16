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
            v42Document.addProperty("documentId", CommonUtil.readElementAsString(v32document, "id"));
            v42Document.addProperty("type", CommonUtil.readElementAsString(v32document, "type"));
            v42Document.addProperty("name", CommonUtil.readElementAsString(v32document, "name"));
            v42Document.addProperty("summary", CommonUtil.readElementAsString(v32document, "summary"));
            v42Document.addProperty("sourceType", CommonUtil.readElementAsString(v32document, "sourceType"));
            v42Document.addProperty("visibility", CommonUtil.readElementAsString(v32document, "visibility"));

            String sourceType = CommonUtil.readElementAsString(v32document, "sourceType");
            if ("FILE".equals(sourceType)) {
                v42Document.addProperty("fileName", CommonUtil.readElementAsString(v32document, "filePath"));
            } else if ("URL".equals(sourceType)) {
                v42Document.addProperty("sourceUrl", CommonUtil.readElementAsString(v32document, "sourceUrl"));
            }
            v42documents.add(v42Document);
        }
        return v42documents;
    }
}
