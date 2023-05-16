package org.wso2.carbon.apimgt.ctl.artifact.converter;

import org.wso2.carbon.apimgt.ctl.artifact.converter.dto.ErrorDTO;
import java.io.File;
import org.wso2.carbon.apimgt.ctl.artifact.converter.CtlArtifactApiService;
import org.wso2.carbon.apimgt.ctl.artifact.converter.impl.CtlArtifactApiServiceImpl;
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
@Path("/ctl-artifact")

@Api(description = "the ctl-artifact API")




public class CtlArtifactApi  {

  @Context MessageContext securityContext;

CtlArtifactApiService delegate = new CtlArtifactApiServiceImpl();


    @POST
    @Path("/convert")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/zip", "application/json" })
    @ApiOperation(value = "Convert export API artifacts from one version to another ", notes = "This operation allows to convert CTL artifacts from one version to another.  Note : As of now only 3.2.0 to 4.2.0 conversion is supported. ", response = File.class, tags={ "Migrate Artifact" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. List of qualifying APIs is returned. ", response = File.class),
        @ApiResponse(code = 403, message = "Forbidden. The request must be conditional but no condition has been specified.", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Not Found. The specified resource does not exist.", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error.", response = ErrorDTO.class) })
    public Response convertCTLArtifact( @Multipart(value = "file") InputStream fileInputStream, @Multipart(value = "file" ) Attachment fileDetail, @ApiParam(value = "Source artifact version ", allowableValues="3.2.0", defaultValue="3.2.0") @DefaultValue("3.2.0")  @QueryParam("srcVersion") String srcVersion, @ApiParam(value = "Target artifact version ", allowableValues="4.2.0", defaultValue="4.2.0") @DefaultValue("4.2.0")  @QueryParam("targetVersion") String targetVersion, @ApiParam(value = "Export artifact format (YAML or JSON) ", allowableValues="YAML, JSON", defaultValue="YAML") @DefaultValue("YAML")  @QueryParam("exportFormat") String exportFormat, @ApiParam(value = "Export artifact type (API or APIProduct) ", allowableValues="API, APIProduct", defaultValue="API") @DefaultValue("API")  @QueryParam("type") String type) throws CTLArtifactConversionException {
        return delegate.convertCTLArtifact(fileInputStream, fileDetail, srcVersion, targetVersion, exportFormat, type, securityContext);
    }
}
