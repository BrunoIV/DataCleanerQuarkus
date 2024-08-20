package org.acme.controllers;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.rest.GridRest;
import org.acme.service.GridService;
import org.acme.service.FileService;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import java.util.List;
import java.util.Map;

@Path("/file")
public class FileController {

    @Inject
    FileService fileService;

    @Inject
    GridService gridService;

    @GET
    @Path("/getExampleData")
    @Produces(MediaType.APPLICATION_JSON)
    public GridRest getExampleData() {
        return gridService.getGrid();
    }

    @POST
    @Path("/import/csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importCsv(MultipartFormDataInput input) {
        GridRest rs = fileService.multipartCsvToGrid(input);
        if (rs == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing file").build();
        }
        this.gridService.setGrid(rs);
        return Response.ok(rs).build();
    }


    @POST
    @Path("/import/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importJson(MultipartFormDataInput input) {
        GridRest rs = fileService.multipartJsonToGrid(input);
        this.gridService.setGrid(rs);
        return Response.ok(this.gridService.getGrid()).build();
    }

    @GET
    @Path("/export/json")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportJson() {
        String json = this.fileService.exportAsJson(this.gridService.getGrid());

        return Response.ok(json)
                .header("Content-Disposition", "attachment; filename=\"data.json\"")
                .build();
    }

    @GET
    @Path("/export/csv")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportCsv() {
        String csv = this.fileService.exportAsCsv(this.gridService.getGrid());


        return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=\"data.csv\"")
                .build();    }

    @GET
    @Path("/export/html")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportHtml() {
        List<Map<String, Object>> lst = this.gridService.getGrid().getValues();

        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\">");
        for (Map<String, Object> data: lst) {
            sb.append("<tr>");

            for (Map.Entry<String, Object> entry : data.entrySet()) {
               sb.append("<td>");
               sb.append(entry.getValue());
               sb.append("</td>");
            }

            sb.append("</tr>");

        }
        sb.append("</table>");

        return Response.ok(sb.toString())
                .header("Content-Disposition", "attachment; filename=\"data.html\"")
                .build();

    }

}
