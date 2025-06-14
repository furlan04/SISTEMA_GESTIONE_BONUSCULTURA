package it.unimib.sd2025;

import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("saldo-rimasto")
public class SaldoRimastoResource {
    @GET
    @Path("/{cf}")
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
}