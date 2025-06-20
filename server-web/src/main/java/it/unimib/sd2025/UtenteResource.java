package it.unimib.sd2025;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import it.unimib.sd2025.Repository.UtenteRepository;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

@Path("utente")
public class UtenteResource {
    private static final Jsonb jsonb = JsonbBuilder.create();
    @GET
    @Path("/{cf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInfo(@PathParam("cf") String cf) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            UtenteRepository userRepository = new UtenteRepository(dbConnection);
            Utente utente = userRepository.get(cf);
            
            return Response.ok(jsonb.toJson(utente)).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }
   

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String userJson) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            Utente userData = JsonbBuilder.create()
                    .fromJson(userJson, Utente.class);
            UtenteRepository userRepository = new UtenteRepository(dbConnection);
            Utente responseUser = userRepository.create(userData);

            return Response.status(Status.CREATED)
                    .entity(jsonb.toJson(responseUser))
                    .build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{cf}/saldo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaldoUtente(@PathParam("cf") String cf) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            UtenteRepository userRepository = new UtenteRepository(dbConnection);
            SaldoRimasto saldoRimasto = userRepository.getSaldoRimastoUtente(cf);
            return Response.ok(jsonb.toJson(saldoRimasto))
                    .build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }
}