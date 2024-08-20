package org.acme.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.acme.model.rest.GridRest;
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
}
