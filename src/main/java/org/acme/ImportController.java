package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.rest.GridRest;
import org.acme.service.GridService;
import org.acme.service.ImportService;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/import")
public class ImportController {

    @Inject
    ImportService importService;

    @Inject
    GridService gridService;

    @GET
    @Path("/getExampleData")
    @Produces(MediaType.APPLICATION_JSON)
    public GridRest getExampleData() {
        return gridService.getGrid();
    }

    @POST
    @Path("/csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importCsv(MultipartFormDataInput input) {
        GridRest rs = importService.multipartCsvToGrid(input);
        if(rs == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing file").build();
        }
        this.gridService.setGrid(rs);
        return Response.ok(rs).build();
    }


    @POST
    @Path("/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importJson(MultipartFormDataInput input) {
        GridRest rs = importService.multipartJsonToGrid(input);
        this.gridService.setGrid(rs);
        return Response.ok(this.gridService.getGrid()).build();
    }

}
