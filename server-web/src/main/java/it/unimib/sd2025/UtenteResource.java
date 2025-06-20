package it.unimib.sd2025;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import it.unimib.sd2025.Repository.BuonoRepository;
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
    @GET
    @Path("/{cf}/totaleConsumato")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTotaleConsumato(@PathParam("cf") String cf) throws Exception {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            UtenteRepository utenteRepo = new UtenteRepository(dbConnection);
            String buoni = utenteRepo.getBuoniUtente(cf);
            BuonoRepository buonoRepo = new BuonoRepository(dbConnection);
            String[] ids = buoni.split(":");
            Double totale = 0.0;
            for (int i = 0; i < ids.length; i++) {
                if (ids[i].isEmpty()) {
                    continue; // Skip empty IDs
                }
                Buono buono = buonoRepo.get(ids[i]);
                if (buono.getDataConsumo() != null) {
                    totale += buono.getValore();
                }
            }
            return Response.ok(JsonbBuilder.create().toJson(totale.toString())).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

  
}