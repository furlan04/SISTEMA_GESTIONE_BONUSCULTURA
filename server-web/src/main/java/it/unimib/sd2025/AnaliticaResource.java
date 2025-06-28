package it.unimib.sd2025;

import java.io.IOException;

import it.unimib.sd2025.Model.Analitica;
import it.unimib.sd2025.Model.Errore;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * AnaliticaResource provides an endpoint to retrieve analytics data
 * about users and vouchers in the system.
 */
@Path("analitica")
public class AnaliticaResource {
    static Jsonb jsonb = JsonbBuilder.create();

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        try (DatabaseConnection dbConnection = new DatabaseConnection()){

            String allKeysResponse = dbConnection.sendDatabaseCommand("getallkeys");
            System.out.println("All keys retrieved: " + allKeysResponse);
            String[] keys = allKeysResponse.split(";");

            int utenti = 0, buoniTotali = 0, buoniConsumati = 0, buoniNonConsumati = 0;
            double spesi = 0, nonSpesi = 0;

            for (String key : keys) {
                // Count number of users
                if (key.matches("^utente:[A-Z0-9]+:nome$")) {
                    utenti++;
                }

                // Count total vouchers
                // categorize them into consumed and non-consumed
                // and calculate the total spent and not spent amounts
                if (key.matches("^buono:\\d+:valore$")) {
                    String valoreStr = dbConnection.sendDatabaseCommand("get " + key).trim();
                    double valore = Double.parseDouble(valoreStr);
                    buoniTotali++;

                    String buonoId = key.split(":")[1];
                    String consumoKey = "buono:" + buonoId + ":dataConsumo";

                    boolean haConsumo = false;
                    for (String k : keys) {
                        if (k.equals(consumoKey)) {
                            String dataConsumo = dbConnection.sendDatabaseCommand("get " + consumoKey).trim();
                            haConsumo = !dataConsumo.isEmpty();
                            break;
                        }
                    }

                    if (haConsumo) {
                        buoniConsumati++;
                        spesi += valore;
                    } else {
                        buoniNonConsumati++;
                        nonSpesi += valore;
                    }
                }
            }

            double totale = 500*utenti;
            double disponibili = totale - spesi - nonSpesi;

            Analitica result = new Analitica();
            result.setUtentiRegistrati(utenti);
            result.setBuoniTotali(buoniTotali);
            result.setBuoniConsumati(buoniConsumati);
            result.setBuoniNonConsumati(buoniNonConsumati);
            result.setContributiSpesi(spesi);
            result.setContributiAssegnati(nonSpesi);
            result.setContributiDisponibili(disponibili);

            return Response.ok(result).build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonb.toJson(new Errore(e.getMessage())))
                    .build();
        }
    }
}