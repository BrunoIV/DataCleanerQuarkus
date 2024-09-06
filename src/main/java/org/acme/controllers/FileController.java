package org.acme.controllers;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.rest.FileRest;
import org.acme.model.rest.GridRest;
import org.acme.service.FileService;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.util.List;

/**
 * Handles importing, exporting, creating, modifying and deleting files
 */
@Path("/file")
public class FileController {

    @Inject
    FileService fileService;

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
    public boolean renameFile(@FormParam("id") int id, @FormParam("name") String name) {
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
    @Path("/export/json/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response exportJson(@PathParam("id") int idFile) {
        String json = this.fileService.exportAsJson(idFile);

        return Response.ok(json)
                .header("Content-Disposition", "attachment; filename=\"data.json\"")
                .build();
    }

    @GET
    @Path("/export/csv/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response exportCsv(@PathParam("id") int idFile) {
        String csv = this.fileService.exportAsCsv(idFile);

        return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=\"data.csv\"")
                .build();
    }

    @GET
    @Path("/export/html/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response exportHtml(@PathParam("id") int idFile) {
        String csv = this.fileService.exportAsHtml(idFile);

        return Response.ok(csv)
                .header("Content-Disposition", "attachment; filename=\"data.html\"")
                .build();

    }

}
