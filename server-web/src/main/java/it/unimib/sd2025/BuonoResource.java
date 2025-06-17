package it.unimib.sd2025;

import java.sql.Date;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("buono")
public class BuonoResource {
    static Jsonb jsonb = JsonbBuilder.create(); 
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuonoInfo(@PathParam("id") String id) {
        try {
            BuonoRepository buonoRepository = new BuonoRepository();
            Buono buono = buonoRepository.getBuono(id);
            String json = jsonb.toJson(buono);
            System.out.println("User info retrieved: " + json);

            return Response.ok(json)
               .type(MediaType.APPLICATION_JSON)
               .build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(e.getMessage())
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
        Buono buonoData = jsonb.fromJson(buonoJson, Buono.class);

        UtenteRepository userRepository = new UtenteRepository();
        BuonoRepository buonoRepository = new BuonoRepository();

        try {
            if (!userRepository.existsUtente(cf)) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("cf " + cf + " does not exist")
                        .build();

            }
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Invalid user data format")
                    .build();
        }
        try {
            SaldoRimasto sr = userRepository.getSaldoRimastoUtente(cf);
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
            buonoData.setDataCreazione(new Date(System.currentTimeMillis()));
            buonoData = buonoRepository.createBuono(buonoData);
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
        try {
            userRepository.addBuonoUtente(cf, buonoData.getId());
        
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.status(Status.CREATED)
                .entity(jsonb.toJson(buonoData))
                .build();
    }

    @DELETE
    @Path("/{cf}/{id}")
    public Response deleteBuono(@PathParam("id") String id, @PathParam("cf") String cf) {
        if (id == null || id.isEmpty() || cf == null || cf.isEmpty()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Buono ID and CF cannot be null or empty ")
                    .build();
        }
        try {
            BuonoRepository buonoRepository = new BuonoRepository();
            UtenteRepository userRepository = new UtenteRepository();
            if (!userRepository.existsUtente(cf)) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("User with Codice Fiscale " + cf + " does not exist")
                        .build();
            }
            if(!userRepository.getBuoniUtente(cf).contains(id)) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("User does not own the Buono with ID: " + id)
                        .build();
            }
            Buono deleted_buono;
            try{
                deleted_buono = buonoRepository.deleteBuono(id);
                userRepository.removeBuonoUtente(cf, id);
            }
            catch(Exception e) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Failed to delete Buono: " + e.getMessage())
                        .build();
            }

            return Response.ok(jsonb.toJson(deleted_buono))
               .type(MediaType.APPLICATION_JSON)
               .build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}/consuma")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consuma(@PathParam("id") String id) {
        try {
            BuonoRepository buonoRepository = new BuonoRepository();
            Buono buono = buonoRepository.consumaBuono(id);
            return Response.ok(jsonb.toJson(buono))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyBuono(@PathParam("id") String id, String buonoJson) {
        if (id == null || id.isEmpty() || buonoJson == null || buonoJson.isEmpty()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Buono ID and data cannot be null or empty")
                    .build();
        }
        BuonoRepository buonoRepository = new BuonoRepository();
        try {
            Buono updatedBuono = jsonb.fromJson(buonoJson, Buono.class);
            updatedBuono = buonoRepository.updateBuono(id, updatedBuono);
            return Response.ok(jsonb.toJson(updatedBuono))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
        
    }


}