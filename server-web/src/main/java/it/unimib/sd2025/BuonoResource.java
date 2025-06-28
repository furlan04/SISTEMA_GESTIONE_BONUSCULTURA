package it.unimib.sd2025;

import java.io.IOException;
import java.sql.Date;

import it.unimib.sd2025.Model.Buono;
import it.unimib.sd2025.Model.Errore;
import it.unimib.sd2025.Model.SaldoRimasto;
import it.unimib.sd2025.Repository.BuonoRepository;
import it.unimib.sd2025.Repository.UtenteRepository;
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

/**
 * BuonoResource is a JAX-RS resource class that provides RESTful endpoints for
 * managing Buono (voucher) information.
 * It allows for creating, retrieving, updating, deleting, and consuming
 * vouchers.
 */
@Path("buono")
public class BuonoResource {
    private static Jsonb jsonb = JsonbBuilder.create();
    private static Object lock = new Object();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBuonoInfo(@PathParam("id") String id) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            BuonoRepository buonoRepository = new BuonoRepository(dbConnection);
            Buono buono = buonoRepository.get(id);
            String json = jsonb.toJson(buono);
            System.out.println("User info retrieved: " + json);

            return Response.ok(json)
                    .type(MediaType.APPLICATION_JSON)
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{cf}")
    public Response createBuono(String buonoJson, @PathParam("cf") String cf) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            if (buonoJson == null || buonoJson.isEmpty() || cf == null || cf.isEmpty()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(jsonb.toJson(new Errore("Buono data and Codice Fiscale cannot be null or empty")))
                        .build();
            }
            Buono buonoData = jsonb.fromJson(buonoJson, Buono.class);

            UtenteRepository userRepository = new UtenteRepository(dbConnection);
            BuonoRepository buonoRepository = new BuonoRepository(dbConnection);
            synchronized (lock) {

                try {
                    if (!userRepository.exists(cf)) {
                        return Response.status(Status.BAD_REQUEST)
                                .entity(jsonb.toJson(new Errore("User with Codice Fiscale " + cf + " does not exist")))
                                .build();

                    }
                } catch (Exception e) {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(jsonb.toJson(new Errore(e.getMessage())))
                            .build();
                }
                try {
                    SaldoRimasto sr = userRepository.getSaldoRimastoUtente(cf);
                    if (buonoData.getValore() <= 0 || sr.getSaldo() < buonoData.getValore()) {
                        return Response.status(Status.BAD_REQUEST)
                                .entity(jsonb.toJson(new Errore(
                                        "Buono value must be greater than zero and less than or equal to user's remaining balance")))
                                .build();
                    }
                } catch (Exception e) {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(jsonb.toJson(new Errore(e.getMessage())))
                            .build();
                }

                try {
                    buonoData.setDataCreazione(new Date(System.currentTimeMillis()));
                    buonoData = buonoRepository.create(buonoData);
                } catch (Exception e) {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(jsonb.toJson(new Errore(e.getMessage())))
                            .build();
                }
                try {
                    userRepository.addBuonoUtente(cf, buonoData.getId());

                } catch (Exception e) {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(jsonb.toJson(new Errore(e.getMessage())))
                            .build();
                }

                return Response.status(Status.CREATED)
                        .entity(jsonb.toJson(buonoData))
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

    @DELETE
    @Path("/{cf}/{id}")
    public Response deleteBuono(@PathParam("id") String id, @PathParam("cf") String cf) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            if (id == null || id.isEmpty() || cf == null || cf.isEmpty()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(jsonb.toJson(new Errore("Buono ID and Codice Fiscale cannot be null or empty")))
                        .build();
            }
            try {
                BuonoRepository buonoRepository = new BuonoRepository(dbConnection);
                UtenteRepository userRepository = new UtenteRepository(dbConnection);
                if (!userRepository.exists(cf)) {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(jsonb.toJson(new Errore("User with Codice Fiscale " + cf + " does not exist")))
                            .build();
                }
                if (!userRepository.getBuoniUtente(cf).contains(id)) {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(jsonb.toJson(new Errore(
                                    "Buono with ID " + id + " does not exist for user with Codice Fiscale " + cf)))
                            .build();
                }
                Buono deleted_buono;
                try {
                    deleted_buono = buonoRepository.deleteBuono(id);
                    userRepository.removeBuonoUtente(cf, id);
                } catch (Exception e) {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(jsonb.toJson(new Errore("Failed to delete Buono: " + e.getMessage())))
                            .build();
                }

                return Response.ok(jsonb.toJson(deleted_buono))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } catch (Exception e) {
                return Response.status(Status.NOT_FOUND)
                        .entity(jsonb.toJson(new Errore(e.getMessage())))
                        .build();
            }
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
                    .build();
        }
    }

    @PUT
    @Path("/{id}/consuma")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consuma(@PathParam("id") String id) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            BuonoRepository buonoRepository = new BuonoRepository(dbConnection);
            Buono buono = buonoRepository.consumaBuono(id);
            return Response.ok(jsonb.toJson(buono))
                    .type(MediaType.APPLICATION_JSON)
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

    @PUT
    @Path("/{cf}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyBuono(@PathParam("id") String id, @PathParam("cf") String cf, String buonoJson) {
        if (id == null || id.isEmpty() || buonoJson == null || buonoJson.isEmpty()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(jsonb.toJson(new Errore("Buono ID and data cannot be null or empty")))
                    .build();
        }
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            BuonoRepository buonoRepository = new BuonoRepository(dbConnection);
            UtenteRepository utenteRepository = new UtenteRepository(dbConnection);
            if (!utenteRepository.exists(cf)) {
                throw new IOException("Utente con codice fiscale " + cf + " non esiste");
            }
            synchronized (lock) {

                double saldoRimasto = utenteRepository.getSaldoRimastoUtente(cf).getSaldo();
                try {
                    Buono updatedBuono = jsonb.fromJson(buonoJson, Buono.class);
                    Buono oldBuono = buonoRepository.get(id);
                    if (updatedBuono.getValore() > saldoRimasto + oldBuono.getValore()
                            || updatedBuono.getValore() <= 0) {
                        throw new IOException(
                                "Buono value must be greater than zero or exceeds user's remaining balance");
                    }
                    updatedBuono = buonoRepository.updateBuono(id, updatedBuono);
                    return Response.ok(jsonb.toJson(updatedBuono))
                            .type(MediaType.APPLICATION_JSON)
                            .build();
                } catch (Exception e) {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(jsonb.toJson(new Errore(e.getMessage())))
                            .build();
                }
            }
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
                    .build();
        }
    }
}