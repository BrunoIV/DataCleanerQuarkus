package org.acme.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.ValueEditRest;
import org.acme.service.DataService;

import java.util.Map;


@Path("/data")
public class DataController {

    @Inject
    DataService dataService;

    @POST
    @Path("/normalize")
    public GridRest normalize(Map<String, String> data) {
        return dataService.normalize(data.get("functionName"), data.get("columns"));
    }

    @POST
    @Path("/validate")
    public GridRest validate(Map<String, String> data) {
        return dataService.validate(data.get("functionName"), data.get("columns"));
    }


    @POST
    @Path("/modifyValue")
    @Consumes(MediaType.APPLICATION_JSON)
    public GridRest modifyValue(ValueEditRest value) {
        return dataService.modifyValue(value);
    }
}
