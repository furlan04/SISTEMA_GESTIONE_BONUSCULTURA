package it.unimib.sd2025;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.xml.crypto.Data;

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
            DatabaseConnection dbConnection = new DatabaseConnection();
            String userDataNome = dbConnection.sendDatabaseCommand("get utente:" + cf + ":nome");
            String userDataCognome = dbConnection.sendDatabaseCommand("get utente:" + cf + ":cognome");
            String userDataEmail = dbConnection.sendDatabaseCommand("get utente:" + cf + ":email");
            dbConnection.close();

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

            DatabaseConnection dbConnection = new DatabaseConnection();
            dbConnection.sendDatabaseCommand("set utente:" + cf + ":nome " + userData.getNome());
            dbConnection.sendDatabaseCommand("set utente:" + cf + ":cognome " + userData.getCognome());
            dbConnection.sendDatabaseCommand("set utente:" + cf + ":email " + userData.getEmail());
            dbConnection.sendDatabaseCommand("set utente:" + cf + ":buoni ");
            dbConnection.close();

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

            return Response.ok(userJson).build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                         .entity("Invalid user data")
                         .build();
        }
    }
}