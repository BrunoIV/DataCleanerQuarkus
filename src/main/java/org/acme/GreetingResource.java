package org.acme;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadFile(MultipartFormDataInput input) {
        try {
            InputPart inputPart = input.getFormDataMap().get("file").get(0);
            String fileName = inputPart.getHeaders().getFirst("Content-Disposition");

            if (fileName != null) {
                String[] contentDisposition = fileName.split(";");
                for (String cd : contentDisposition) {
                    if (cd.trim().startsWith("filename")) {

                        //Reads the file
                        InputStream inputStream = inputPart.getBody(InputStream.class, null);
                        String fileContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                                .lines().collect(Collectors.joining("\n"));

                        return Response.ok(fileContent).build();
                    }
                }
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error processing file").build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
