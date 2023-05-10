package org.wso2.carbon.apimgt.ctl.artifact.converter;

import org.wso2.carbon.apimgt.ctl.artifact.converter.dto.ErrorDTO;
import java.io.File;
import org.wso2.carbon.apimgt.ctl.artifact.converter.MigrateApiService;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.MigrateApiServiceImpl;
import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import io.swagger.annotations.*;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
@Path("/migrate")

@Api(description = "the migrate API")




public class MigrateApi  {

  @Context MessageContext securityContext;

MigrateApiService delegate = new MigrateApiServiceImpl();


    @POST
    
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/zip", "application/json" })
    @ApiOperation(value = "Migrate export API artifacts from one version to another ", notes = "This operation provides you a list of available APIs qualifying under a given search condition.  Each retrieved API is represented with a minimal amount of attributes. If you want to get complete details of an API, you need to use **Get details of an API** operation. ", response = File.class, tags={ "Migrate Artifact" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. List of qualifying APIs is returned. ", response = File.class),
        @ApiResponse(code = 403, message = "Forbidden. The request must be conditional but no condition has been specified.", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Not Found. The specified resource does not exist.", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error.", response = ErrorDTO.class) })
    public Response migrateArtifact( @Multipart(value = "file") InputStream fileInputStream, @Multipart(value = "file" ) Attachment fileDetail, @ApiParam(value = "Source artifact version ", defaultValue="3.2.0") @DefaultValue("3.2.0")  @QueryParam("srcVersion") String srcVersion, @ApiParam(value = "Target artifact version ", defaultValue="4.2.0") @DefaultValue("4.2.0")  @QueryParam("targetVersion") String targetVersion) throws CTLArtifactConversionException {
        return delegate.migrateArtifact(fileInputStream, fileDetail, srcVersion, targetVersion, securityContext);
    }
}
