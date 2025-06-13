package it.unimib.sd2025;

import java.io.IOException;

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

        try{
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
        try {
            String response = buonoRepository.createBuono(
                    buonoData.getId(),
                    buonoData.getValore(),
                    buonoData.getTipologia(),
                    buonoData.getDataCreazione().toString(),
                    buonoData.getDataConsumo().toString());

            if (response.startsWith("ERROR")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(response)
                        .build();
            }
        } catch (IOException e1) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Invalid buono data format")
                    .build();
        }
        try {
            if (userRepository.addBuonoUtente(cf, buonoData.getId()).startsWith("ERROR")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Failed to add Buono to user")
                        .build();
            }
        } catch (Exception e2) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Failed to add Buono to user: " + e2.getMessage())
                    .build();
        }

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
            String response = buonoRepository.deleteBuono(id);
            if (response.startsWith("ERROR")) {
                return Response.status(Status.NOT_FOUND)
                        .entity(response)
                        .build();
            }

            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Buono not found: " + id)
                    .build();
        }
    }
}