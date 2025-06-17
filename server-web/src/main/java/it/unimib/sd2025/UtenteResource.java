package it.unimib.sd2025;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.json.bind.JsonbBuilder;

@Path("utente")
public class UtenteResource {

    @GET
    @Path("/{cf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInfo(@PathParam("cf") String cf) {
        try {
            UtenteRepository userRepository = new UtenteRepository();
            String json = userRepository.getUtente(cf);

            System.out.println("User info retrieved: " + json);

            return Response.ok(json).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("User not found: ")
                    .build();
        }
    }
   

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String userJson) {
        try {
            Utente userData = JsonbBuilder.create()
                    .fromJson(userJson, Utente.class);

            UtenteRepository userRepository = new UtenteRepository();
            String response = userRepository.createUtente(userData.getCodiceFiscale(), userData.getNome(), 
                userData.getCognome(), userData.getEmail());

            if (response.startsWith("ERROR")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(response)
                        .build();
            }

            return Response.status(Status.CREATED)
                    .entity(userJson)
                    .build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Invalid user data")
                    .build();
        }
    }

    @DELETE
    @Path("/{cf}")
    public Response deleteUser(@PathParam("cf") String cf) {
        try {
            UtenteRepository userRepository = new UtenteRepository();
            String response = userRepository.deleteUtente(cf);

            if (response.startsWith("ERROR")) {
                return Response.status(Status.NOT_FOUND)
                        .entity(response)
                        .build();
            }

            String[] parts = response.split(":");

            if (parts.length > 0) {
                BuonoRepository buonoRepository = new BuonoRepository();
                for (String buonoId : parts) {
                    if (!buonoId.isEmpty()) {
                        buonoRepository.deleteBuono(buonoId);
                    }
                }
            }

            return Response.status(Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("User not found: " + cf)
                    .build();
        }
    }

    @GET
    @Path("/{cf}/saldo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaldoUtente(@PathParam("cf") String cf) {
        try {
            UtenteRepository utenteRepo = new UtenteRepository();
            
            String saldoRimasto = utenteRepo.getSaldoRimastoUtente(cf);

            return Response.ok(saldoRimasto)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Buono not found: ")
                    .build();
        }
    }
    /*
     * @PUT
     * 
     * @Path("/{cf}")
     * 
     * @Consumes(MediaType.APPLICATION_JSON)
     * 
     * @Produces(MediaType.APPLICATION_JSON)
     * public Response updateUser(@PathParam("cf") String cf, String userJson) {
     * try {
     * Map<String, String> userData = JsonbBuilder.create()
     * .fromJson(userJson, new HashMap<String,
     * String>(){}.getClass().getGenericSuperclass());
     * 
     * return Response.ok(userJson).build();
     * } catch (Exception e) {
     * return Response.status(Status.BAD_REQUEST)
     * .entity("Invalid user data")
     * .build();
     * }
     * }
     */
}