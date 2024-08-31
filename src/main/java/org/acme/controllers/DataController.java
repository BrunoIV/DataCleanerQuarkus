package org.acme.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.ValueEditRest;
import org.acme.service.DataService;
import org.acme.util.Utils;

import java.util.Map;


/**
 * Handles the retrieval, validation and modification of data
 */
@Path("/data")
public class DataController {

    @Inject
    DataService dataService;

    @GET
    @Path("/getData/{idFile}")
    public GridRest getData(@PathParam("idFile") int idFile) {
        return dataService.getData(idFile);
    }

    @POST
    @Path("/normalize")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest normalize(@FormParam("functionName") String functionName,
                              @FormParam("columns") String columns,
                              @FormParam("idFile") int idFile) {
        return dataService.normalize(functionName, Utils.text2IntArray(columns), idFile);
    }


    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest validate(@FormParam("functionName") String functionName,
                              @FormParam("columns") String columns,
                              @FormParam("idFile") int idFile) {
        return dataService.validate(functionName, Utils.text2IntArray(columns), idFile);
    }

    @POST
    @Path("/modifyValue")
    @Consumes(MediaType.APPLICATION_JSON)
    public GridRest modifyValue(ValueEditRest value) {
        return dataService.modifyValue(value);
    }
}
