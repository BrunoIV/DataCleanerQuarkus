package org.acme.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.rest.GridRest;
import org.acme.service.StructureService;

@Path("/structure")
public class StructureController {

    @Inject
    StructureService structureService;

    @POST
    @Path("/addColumn")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest addColumn(@FormParam("name") String name, @FormParam("position") int position) {
        return structureService.addColumn(name, position);
    }

    @POST
    @Path("/addRow")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest addRow(@FormParam("position") int position) {
        return structureService.addRow(position);
    }
}
