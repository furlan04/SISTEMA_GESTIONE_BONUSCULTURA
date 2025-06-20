package it.unimib.sd2025;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("buoni")
public class BuoniResource {
    static Jsonb jsonb = JsonbBuilder.create();

    @GET
    @Path("/{cf}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@PathParam("cf") String cf) {
        try {
            UtenteRepository utenteRepo = new UtenteRepository();
            String buoni = utenteRepo.getBuoniUtente(cf);

            System.out.println("Buoni retrieved for user " + cf + ": " + buoni);

            String[] ids = buoni.split(":");
            Buono[] buoniList = new Buono[ids.length];
            for (int i = 0; i < ids.length; i++) {
                if (ids[i].isEmpty()) {
                    continue; // Skip empty IDs
                }
                BuonoRepository buonoRepo = new BuonoRepository();
                Buono buono = buonoRepo.getBuono(ids[i]);
                buoniList[i] = buono;
            }

            return Response.ok(JsonbBuilder.create().toJson(buoniList)).build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    
}