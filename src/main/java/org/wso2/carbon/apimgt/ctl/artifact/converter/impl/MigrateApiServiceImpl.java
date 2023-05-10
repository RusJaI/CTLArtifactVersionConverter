package org.wso2.carbon.apimgt.ctl.artifact.converter.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.ctl.artifact.converter.*;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;
import org.wso2.carbon.apimgt.ctl.artifact.converter.util.CommonUtil;

import java.io.File;

import java.io.IOException;

import java.io.InputStream;

import javax.ws.rs.core.Response;


public class MigrateApiServiceImpl implements MigrateApiService {
    private static final Log log = LogFactory.getLog(MigrateApiServiceImpl.class);

    public Response migrateArtifact(InputStream fileInputStream, Attachment fileDetail, String srcVersion,
                                    String targetVersion, MessageContext messageContext) {
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

            String format = Constants.YAML_FORMAT;
            ArtifactConversionManager conversionManager = new ArtifactConversionManager(srcVersion, targetVersion,
                    srcArtifactPath, targetArtifactPath, format);
            conversionManager.convert();
        } catch (CTLArtifactConversionException | IOException e) {
            //send error response
        }

        return Response.ok().entity("magic!").build();
    }
}
