package it.unimib.sd2025;

import java.io.IOException;
import java.sql.Date;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("buono")
public class BuonoResource {
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuonoInfo(@PathParam("id") String id) {
        try {
            BuonoRepository buonoRepository = new BuonoRepository();
            String json = buonoRepository.getBuono(id);
            System.out.println("User info retrieved: " + json);

            return Response.ok(json).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Buono not found: ")
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{cf}")
    public Response createBuono(String buonoJson, @PathParam("cf") String cf) {
        if (buonoJson == null || buonoJson.isEmpty() || cf == null || cf.isEmpty()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Buono data cannot be null or empty")
                    .build();
        }
        Buono buonoData = JsonbBuilder.create()
                .fromJson(buonoJson, Buono.class);

        UtenteRepository userRepository = new UtenteRepository();
        BuonoRepository buonoRepository = new BuonoRepository();

        try {
            String userCheck = userRepository.existsUtente(cf);
            if (userCheck.startsWith("ERROR")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(userCheck)
                        .build();

            }
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Invalid user data format")
                    .build();
        }
        String new_id = String.valueOf(Id.getNextId());
        try {
            SaldoRimasto sr = JsonbBuilder.create().fromJson(userRepository.getSaldoRimastoUtente(cf),
                    SaldoRimasto.class);
            if (buonoData.getValore() <= 0 || sr.getSaldo() < buonoData.getValore()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Buono value must be greater than zero or exceeds user's remaining balance")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Failed to retrieve user's remaining balance: " + e.getMessage())
                    .build();
        }
        try {
            String create_response = buonoRepository.createBuono(
                    new_id,
                    buonoData.getValore(),
                    buonoData.getTipologia(),
                    (new Date(System.currentTimeMillis())).toString());

            while (create_response.startsWith("ERROR: Buono with ID")) {
                new_id = String.valueOf(Id.getNextId());
                create_response = buonoRepository.createBuono(
                        new_id,
                        buonoData.getValore(),
                        buonoData.getTipologia(),
                        (new Date(System.currentTimeMillis())).toString());
            }
            if (create_response.startsWith("ERROR")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("CREATION " + create_response)
                        .build();
            }
        } catch (IOException e1) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Invalid buono data format")
                    .build();
        }
        try {
            String add_response = userRepository.addBuonoUtente(cf, new_id);
            if (add_response.startsWith("ERROR")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("ADDITION " + add_response)
                        .build();
            }
        } catch (Exception e2) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Failed to add Buono to user: " + e2.getMessage())
                    .build();
        }

        buonoData.setId(new_id);
        buonoJson = JsonbBuilder.create().toJson(buonoData);
        return Response.status(Status.CREATED)
                .entity(buonoJson)
                .build();
    }

    @DELETE
    @Path("/{cf}/{id}")
    public Response deleteBuono(@PathParam("id") String id, @PathParam("cf") String cf) {
        if (id == null || id.isEmpty()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Buono ID cannot be null or empty")
                    .build();
        }
        try {
            BuonoRepository buonoRepository = new BuonoRepository();
            UtenteRepository userRepository = new UtenteRepository();
            String userCheck = userRepository.existsUtente(cf);
            if (userCheck.startsWith("ERROR")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(userCheck)
                        .build();
            }
            if (userRepository.removeBuonoUtente(cf, id).startsWith("ERROR")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Failed to remove Buono from user")
                        .build();
            }
            String create_response = buonoRepository.deleteBuono(id);
            if (create_response.startsWith("ERROR")) {
                return Response.status(Status.NOT_FOUND)
                        .entity(create_response)
                        .build();
            }

            return Response.ok(create_response).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Buono not found: " + id)
                    .build();
        }
    }
}