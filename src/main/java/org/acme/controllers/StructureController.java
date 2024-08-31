package org.acme.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.rest.GridRest;
import org.acme.service.StructureService;
import org.acme.util.Utils;

@Path("/structure")
public class StructureController {

    @Inject
    StructureService structureService;

    @POST
    @Path("/addColumn")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest addColumn(@FormParam("name") String name, @FormParam("position") int position, @FormParam("idFile") int idFile) {
        return structureService.addColumn(name, position, idFile);
    }

    @POST
    @Path("/addRow")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest addRow(@FormParam("position") int position, @FormParam("idFile") int idFile) {
        return structureService.addRow(position, idFile);
    }

    @POST
    @Path("/deleteRows")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest deleteRows(@FormParam("indexes") String indexes, @FormParam("idFile") int idFile) {
        return structureService.deleteRows(Utils.text2IntArray(indexes), idFile);
    }


    @POST
    @Path("/deleteColumns")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest deleteColumns(@FormParam("indexes") String indexes, @FormParam("idFile") int idFile) {
        return structureService.deleteColumns(Utils.text2IntArray(indexes), idFile);
    }

    @POST
    @Path("/joinColumns")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest joinColumns(@FormParam("indexes") String indexes, @FormParam("idFile") int idFile) {
        return structureService.joinColumns(Utils.text2IntArray(indexes), idFile);
    }


}
