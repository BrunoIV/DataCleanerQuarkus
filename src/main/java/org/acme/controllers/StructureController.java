package org.acme.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.rest.ColumnEditRest;
import org.acme.model.rest.GridRest;
import org.acme.service.StructureService;


@Path("/structure")
public class StructureController {

    @Inject
    StructureService structureService;

    @POST
    @Path("/modifyColumn")
    @Consumes(MediaType.APPLICATION_JSON)
    public GridRest modifyColumn(ColumnEditRest attributes) {
        return structureService.modifyColumn(attributes);
    }

}
