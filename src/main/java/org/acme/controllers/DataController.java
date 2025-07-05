package org.acme.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.rest.ChangesRest;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.ValidationRest;
import org.acme.model.rest.ValueEditRest;
import org.acme.service.DataService;
import org.acme.util.Utils;

import java.util.List;

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

    @GET
    @Path("/getData/history/{idHistory}")
    public GridRest getDataHistory(@PathParam("idHistory") int idHistory) {
        return dataService.getDataHistory(idHistory);
    }

    @GET
    @Path("/getHistory/{id}")
    public List<ChangesRest> getHistory(@PathParam("id") int idFile) {
        return dataService.getHistory(idFile);
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
    public List<ValidationRest> validate(@FormParam("functionName") String functionName,
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

    @POST
    @Path("/fillAutoIncremental")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest fillAutoIncremental(
                              @FormParam("columns") String columns,
                              @FormParam("idFile") int idFile) {
        return dataService.fillAutoIncremental(Utils.text2IntArray(columns), idFile);
    }


    @POST
    @Path("/fillFixedValue")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest fillFixedValue(@FormParam("newValue") String newValue,
                                   @FormParam("columns") String columns,
                                   @FormParam("idFile") int idFile) {
        return dataService.fillFixedValue(newValue, Utils.text2IntArray(columns), idFile);
    }


    @POST
    @Path("/searchNumber")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest searchNumber(@FormParam("min") int min,
                           @FormParam("max") int max,
                                   @FormParam("columns") String columns,
                                   @FormParam("idFile") int idFile) {
        return dataService.searchNumber(min, max, Utils.text2IntArray(columns), idFile);
    }

    @POST
    @Path("/searchText")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public GridRest searchText(@FormParam("value") String value,
                           @FormParam("typeSearch") int typeSearch,
                           @FormParam("columns") String columns,
                           @FormParam("idFile") int idFile) {
        return dataService.searchText(value, typeSearch, Utils.text2IntArray(columns), idFile);
    }
}
