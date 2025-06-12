package it.unimib.sd2025;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            String userDataNome = DatabaseConnection.sendDatabaseCommand("get utente:" + cf + ":nome");
            String userDataCognome = DatabaseConnection.sendDatabaseCommand("get utente:" + cf + ":cognome");
            String userDataEmail = DatabaseConnection.sendDatabaseCommand("get utente:" + cf + ":email");

            Utente user = new Utente(userDataNome, userDataCognome, userDataEmail, cf);

            return Response.ok(JsonbBuilder.create().toJson(user)).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                         .entity("User not found: " + cf)
                         .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(String userJson) {
        try {
            Utente userData = JsonbBuilder.create()
                .fromJson(userJson, new HashMap<String, String>(){}.getClass().getGenericSuperclass());

            String cf = userData.getCodiceFiscale();
            DatabaseConnection.sendDatabaseCommand("set utente:" + cf + ":nome " + userData.getNome());
            DatabaseConnection.sendDatabaseCommand("set utente:" + cf + ":cognome " + userData.getCognome());
            DatabaseConnection.sendDatabaseCommand("set utente:" + cf + ":email " + userData.getCognome());

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
            DatabaseConnection.sendDatabaseCommand("delete utente:" + cf + ":nome");
            DatabaseConnection.sendDatabaseCommand("delete utente:" + cf + ":cognome");
            DatabaseConnection.sendDatabaseCommand("delete utente:" + cf + ":email");
            
            return Response.status(Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                         .entity("User not found: " + cf)
                         .build();
        }
    }

    @PUT
    @Path("/{cf}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("cf") String cf, String userJson) {
        try {
            Map<String, String> userData = JsonbBuilder.create()
                .fromJson(userJson, new HashMap<String, String>(){}.getClass().getGenericSuperclass());

            DatabaseConnection.sendDatabaseCommand("set utente:" + cf + ":nome " + userData.get("nome"));
            DatabaseConnection.sendDatabaseCommand("set utente:" + cf + ":cognome " + userData.get("cognome"));
            DatabaseConnection.sendDatabaseCommand("set utente:" + cf + ":email " + userData.get("email"));

            return Response.ok(userJson).build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                         .entity("Invalid user data")
                         .build();
        }
    }
}