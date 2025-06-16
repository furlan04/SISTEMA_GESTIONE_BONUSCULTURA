package it.unimib.sd2025;

import java.io.IOException;
import java.sql.Date;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

public class BuonoRepository extends DatabaseConnection {
    public BuonoRepository() {
        super();
    }

    public String existsBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return "ERROR: ID cannot be null or empty";
        }
        String response = sendDatabaseCommand("exists buono:" + id);
        if (response.startsWith("ERROR")) {
            return "ERROR: Buono with ID " + id + " does not exist";
        }
        return response;
    }

    public String createBuono(String id, double valore, String tipologia, String dataCreazione)
            throws IOException {
        if (id == null || id.isEmpty() || tipologia == null || tipologia.isEmpty() || dataCreazione == null
                || dataCreazione.isEmpty()) {
            return "ERROR: ID, Tipologia, Data Creazione cannot be null or empty";
        }
        if (existsBuono(id).equals("true")) {
            return "ERROR: Buono with ID " + id + " already exists";
        }
        try {
            sendDatabaseCommand("set buono:" + id + ":valore " + valore);
            sendDatabaseCommand("set buono:" + id + ":tipologia " + tipologia);
            sendDatabaseCommand("set buono:" + id + ":dataCreazione " + dataCreazione);
            sendDatabaseCommand("set buono:" + id + ":dataConsumo ");
        } catch (IOException e) {
            return "ERROR: Failed to create Buono with ID " + id + ". " + e.getMessage();
        }
        return "SUCCESS";
    }

    public String getBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return "ERROR: ID cannot be null or empty";
        }

        if (!"true".equals(existsBuono(id))) {
            return "ERROR: Buono with ID " + id + " does not exist";
        }

        String valoreStr = sendDatabaseCommand("get buono:" + id + ":valore");
        String tipologia = sendDatabaseCommand("get buono:" + id + ":tipologia");
        String dataCreazioneStr = sendDatabaseCommand("get buono:" + id + ":dataCreazione");
        String dataScadenzaStr = sendDatabaseCommand("get buono:" + id + ":dataConsumo");

        // Controlla errori nella risposta dal DB
        if (valoreStr.startsWith("ERROR") || tipologia.startsWith("ERROR") ||
                dataCreazioneStr.startsWith("ERROR") || dataScadenzaStr.startsWith("ERROR")) {
            return "ERROR: Buono with ID " + id + " could not be retrieved";
        }

        try {
            double valore = Double.parseDouble(valoreStr);
            Date dataCreazione = Date.valueOf(dataCreazioneStr);
            Date dataScadenza = null;
            try {
                dataScadenza = Date.valueOf(dataScadenzaStr);
            } catch (IllegalArgumentException e) {
                // Se la data di scadenza non è valida, impostala a null
            }

            Buono buono = new Buono(id, valore, tipologia, dataCreazione, dataScadenza);

            JsonbConfig config = new JsonbConfig()
                    .withNullValues(true);

            Jsonb jsonb = JsonbBuilder.create(config);
            return jsonb.toJson(buono);
        } catch (IllegalArgumentException e) {
            return "ERROR: Invalid data format for Buono with ID " + id;
        }
    }

    public String deleteBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return "ERROR: ID cannot be null or empty";
        }
        if (existsBuono(id).equals("false")) {
            return "ERROR: Buono with ID " + id + " does not exist";
        }
        if(!sendDatabaseCommand("get buono:" + id + ":dataConsumo").equals("")) {
            return "ERROR: Buono with ID " + id + " has not been consumed and cannot be deleted";
        }
        String valore = sendDatabaseCommand("delete buono:" + id + ":valore");
        String tipologia = sendDatabaseCommand("delete buono:" + id + ":tipologia");
        String dataCreazione = sendDatabaseCommand("delete buono:" + id + ":dataCreazione");
        sendDatabaseCommand("delete buono:" + id + ":dataConsumo");
        Buono buono = new Buono(id, Double.parseDouble(valore), tipologia, Date.valueOf(dataCreazione),
                null);
        JsonbConfig config = new JsonbConfig()
                .withNullValues(true);

        Jsonb jsonb = JsonbBuilder.create(config);
        return jsonb.toJson(buono);
    }

    public String consumaBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return "ERROR: ID cannot be null or empty";
        }
        if (existsBuono(id).equals("false")) {
            return "ERROR: Buono with ID " + id + " does not exist";
        }
        if(!sendDatabaseCommand("get buono:" + id + ":dataConsumo").equals("")) {
            return "ERROR: Buono with ID " + id + " has already been consumed";
        }
        String dataConsumo = new Date(System.currentTimeMillis()).toString();
        sendDatabaseCommand("set buono:" + id + ":dataConsumo " + dataConsumo);
        return "SUCCESS: Buono with ID " + id + " consumed on " + dataConsumo;
    }
}
