package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class GreetingResource {

    @GET
    @Path("/getExampleData")
    @Produces(MediaType.APPLICATION_JSON)
    public GridRest getExampleData() {
        List<Map<String, Object>> cars = new ArrayList<>();

        Map<String, Object> car1 = new HashMap<>();
        car1.put("make", "Toyota");
        car1.put("model", "Celica");
        car1.put("price", 56655);

        Map<String, Object> car2 = new HashMap<>();
        car2.put("make", "Ford");
        car2.put("model", "Mondeo");
        car2.put("price", 40000);

        Map<String, Object> car3 = new HashMap<>();
        car3.put("make", "Porsche");
        car3.put("model", "Boxster");
        car3.put("price", 900000);

        cars.add(car1);
        cars.add(car2);
        cars.add(car3);

        List<ColumnHeaderRest> lst = new ArrayList<>();
        lst.add(new ColumnHeaderRest("Make", "make", true));
        lst.add(new ColumnHeaderRest("Model", "model", true));
        lst.add(new ColumnHeaderRest("Price", "price", true));

        GridRest rs = new GridRest();
        rs.setValues(cars);
        rs.setHeader(lst);

        return rs;
    }
}
