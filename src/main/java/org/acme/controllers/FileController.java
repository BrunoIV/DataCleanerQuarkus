package org.acme.controllers;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.rest.FileRest;
import org.acme.model.rest.GridRest;
import org.acme.service.GridService;
import org.acme.service.FileService;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles importing, exporting, creating, modifying and deleting files
 */
@Path("/file")
public class FileController {

    @Inject
    FileService fileService;

    @Inject
    GridService gridService;

    @GET
    @Path("/getFiles")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public List<FileRest> getFiles() {
        return fileService.getFiles();
    }

    @POST
    @Path("/createFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public boolean createFile(@FormParam("name") String name) {
        return fileService.createFile(name);
    }

    @POST
    @Path("/deleteFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public boolean deleteFile(@FormParam("id") int id) {
        return fileService.deleteFile(id);
    }

    @POST
    @Path("/renameFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public boolean renameFile(@FormParam("id") int id, @FormParam("id") String name) {
        return fileService.renameFile(id, name);
    }


    @POST
    @Path("/import/csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public GridRest importCsv(MultipartFormDataInput input) {
        return fileService.importCsv(input);
    }


    @POST
    @Path("/import/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public GridRest importJson(MultipartFormDataInput input) {
        return fileService.importJson(input);
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
        List<LinkedHashMap<String, Object>> lst = this.gridService.getGrid().getValues();

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
