package org.wso2.carbon.apimgt.ctl.artifact.converter.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.*;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;
import org.wso2.carbon.apimgt.rest.api.common.RestApiConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.Response;


public class CtlArtifactApiServiceImpl implements CtlArtifactApiService {
    private static final Log log = LogFactory.getLog(CtlArtifactApiServiceImpl.class);

    public Response convertCTLArtifact(InputStream fileInputStream, Attachment fileDetail, String srcVersion,
                                       String targetVersion, String exportFormat, String type, MessageContext messageContext) {
        try {
            log.info("Migrating API artifact from version " + srcVersion + " to " + targetVersion);
            //Transfer input stream to APIArchive.zip inside tmp folder
            File tempDirectory = CommonUtil.createTempDirectory();
            String absolutePathToTempDirectory = tempDirectory.getAbsolutePath() + File.separator;
            CommonUtil.transferFile(fileInputStream, Constants.UPLOAD_FILE_NAME, absolutePathToTempDirectory);

            //Extract APIArchive.zip
            File apiArchive = new File(absolutePathToTempDirectory + Constants.UPLOAD_FILE_NAME);
            String apiArtifactName = CommonUtil.extractArchive(apiArchive, absolutePathToTempDirectory);

            //Copy extracted API artifact to a new location in tmp folder
            String srcArtifactPath = absolutePathToTempDirectory + apiArtifactName;
            File srcAPIArtifactFile = new File(srcArtifactPath);
            File tempTargetDirectory = CommonUtil.createTempDirectory();
            String targetArtifactPath = tempTargetDirectory.getAbsolutePath() + File.separator + apiArtifactName;
            File copyAPIArtifactFile = new File(targetArtifactPath);
            FileUtils.copyDirectory(srcAPIArtifactFile, copyAPIArtifactFile);

            if (Constants.API_PRIDUCT_TYPE.equals(type)) {
                APIProductArtifactConversionManager productConversionManager = new APIProductArtifactConversionManager(srcVersion, targetVersion,
                        srcArtifactPath, targetArtifactPath, exportFormat);
                productConversionManager.convert();
            } else {
                APIArtifactConversionManager apiConversionManager = new APIArtifactConversionManager(srcVersion, targetVersion,
                        srcArtifactPath, targetArtifactPath, exportFormat);
                apiConversionManager.convert();
            }

            CommonUtil.archiveDirectory(tempTargetDirectory.getAbsolutePath());
            FileUtils.deleteQuietly(new File(tempTargetDirectory.getAbsolutePath()));
            File file = new File(tempTargetDirectory.getAbsolutePath() + Constants.ZIP_EXTENSION);
            return Response.ok(file).header(RestApiConstants.HEADER_CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getName() + "\"").build();
        } catch (CTLArtifactConversionException | IOException e) {
            log.error("Error occurred while converting the artifact", e);
            return Response.serverError().entity("Error occurred while converting the artifact").build();
        }
    }
}
