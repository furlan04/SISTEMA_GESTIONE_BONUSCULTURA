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

@Path("buoni")
public class BuoniResource {
    @GET
    @Path("/{cf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@PathParam("cf") String cf) {
        try {
            UtenteRepository utenteRepo = new UtenteRepository();
            String buoni = utenteRepo.getBuoniUtente(cf);

            if (buoni.startsWith("ERROR")) {
                return Response.status(Status.NOT_FOUND)
                        .entity(buoni)
                        .build();
            }

            String[] ids = buoni.split(":");
            Buono[] buoniList = new Buono[ids.length];
            for (int i = 0; i < ids.length; i++) {
                if (ids[i].isEmpty()) {
                    continue; // Skip empty IDs
                }
                BuonoRepository buonoRepo = new BuonoRepository();
                String buonoJson = buonoRepo.getBuono(ids[i]);
                if (buonoJson.startsWith("ERROR")) {
                    return Response.status(Status.NOT_FOUND)
                            .entity("Buono with ID " + ids[i] + " not found")
                            .build();
                }
                buoniList[i] = JsonbBuilder.create().fromJson(buonoJson, Buono.class);
            }

            return Response.ok(JsonbBuilder.create().toJson(buoniList)).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Buono not found: ")
                    .build();
        }
    }
}