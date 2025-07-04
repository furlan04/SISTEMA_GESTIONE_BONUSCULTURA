package it.unimib.sd2025;

import java.io.IOException;

import it.unimib.sd2025.Model.Errore;
import it.unimib.sd2025.Model.SaldoRimasto;
import it.unimib.sd2025.Model.Utente;
import it.unimib.sd2025.Repository.UtenteRepository;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * UtenteResource is a JAX-RS resource class that provides RESTful endpoints for
 * managing user information.
 * It allows clients to retrieve user information by Codice Fiscale, create new
 * users, and get the remaining balance of a user.
 */
@Path("utente")
public class UtenteResource {
    private static final Jsonb jsonb = JsonbBuilder.create();
    private static Object lock = new Object();

    @GET
    @Path("/{cf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInfo(@PathParam("cf") String cf) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            UtenteRepository userRepository = new UtenteRepository(dbConnection);
            Utente utente = userRepository.get(cf);

            return Response.ok(jsonb.toJson(utente)).build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
                    .build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
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
            synchronized (lock) {
                Utente responseUser = userRepository.create(userData);
                return Response.status(Status.CREATED)
                        .entity(jsonb.toJson(responseUser))
                        .build();
            }
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
                    .build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
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
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
                    .build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
                    .build();
        }
    }
}