package org.wso2.carbon.apimgt.ctl.artifact.converter.impl;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.ResourceVersionConverter;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.factory.ResourceFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.DocumentsDirectory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.DocumentsMappingUtil;

import java.util.List;


public class DocumentVersionConverter extends ResourceVersionConverter {
    DocumentsDirectory srcDocuments;
    DocumentsDirectory targetDocuments;

    public DocumentVersionConverter(String srcVersion, String targetVersion, String srcPath, String targetPath,
                                    String exportFormat) {
        super(srcVersion, targetVersion, srcPath, targetPath, exportFormat);
        this.srcDocuments = ResourceFactory.getDocumentsRepresentation(srcVersion);
        this.targetDocuments = ResourceFactory.getDocumentsRepresentation(targetVersion);
    }

    public void convert() throws CTLArtifactConversionException {
        //Import document resources from src artifact
        srcDocuments.importDocuments(srcPath);

        //Map imported src documents to target documents format
        List<JsonObject> mappedDocumentsList = DocumentsMappingUtil.mapDocuments(srcDocuments.getDocuments(),
                srcVersion, targetVersion);
        targetDocuments.setDocuments(mappedDocumentsList);

        //Export mapped documents to target artifact
        targetDocuments.exportDocuments(targetPath, srcPath, exportFormat);
    }
}
