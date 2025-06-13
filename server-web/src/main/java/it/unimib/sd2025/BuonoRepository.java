package it.unimib.sd2025;

import java.io.IOException;
import java.sql.Date;

import jakarta.json.bind.JsonbBuilder;

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
    public String createBuono(String id, double valore, String tipologia, String dataCreazione, String dataScadenza) throws IOException {
        if (id == null || id.isEmpty() || tipologia == null || tipologia.isEmpty() || dataCreazione == null || dataCreazione.isEmpty() || dataScadenza == null || dataScadenza.isEmpty()) {
            return "ERROR: ID, Tipologia, Data Creazione and Data Scadenza cannot be null or empty";
        }
        if (existsBuono(id).equals("true")) {
            return "ERROR: Buono with ID " + id + " already exists";
        }
        sendDatabaseCommand("set buono:" + id + ":valore " + valore);
        sendDatabaseCommand("set buono:" + id + ":tipologia " + tipologia);
        sendDatabaseCommand("set buono:" + id + ":dataCreazione " + dataCreazione);
        sendDatabaseCommand("set buono:" + id + ":dataScadenza " + dataScadenza);
        return "SUCCESS";
    }
    public String getBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return "ERROR: ID cannot be null or empty";
        }
        if (existsBuono(id).equals("false")) {
            return "ERROR: Buono with ID " + id + " does not exist";
        }
        String valore = sendDatabaseCommand("get buono:" + id + ":valore");
        String tipologia = sendDatabaseCommand("get buono:" + id + ":tipologia");
        String dataCreazione = sendDatabaseCommand("get buono:" + id + ":dataCreazione");
        String dataScadenza = sendDatabaseCommand("get buono:" + id + ":dataScadenza");
        Buono buono = new Buono(id, Double.parseDouble(valore), tipologia, Date.valueOf(dataCreazione), Date.valueOf(dataScadenza));
        return JsonbBuilder.create().toJson(buono);
    }
    public String deleteBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return "ERROR: ID cannot be null or empty";
        }
        if (existsBuono(id).equals("false")) {
            return "ERROR: Buono with ID " + id + " does not exist";
        }
        String valore = sendDatabaseCommand("get buono:" + id + ":valore");
        String tipologia = sendDatabaseCommand("get buono:" + id + ":tipologia");
        String dataCreazione = sendDatabaseCommand("get buono:" + id + ":dataCreazione");
        String dataScadenza = sendDatabaseCommand("get buono:" + id + ":dataScadenza");
        sendDatabaseCommand("delete buono:" + id);
        Buono buono = new Buono(id, Double.parseDouble(valore), tipologia, Date.valueOf(dataCreazione), Date.valueOf(dataScadenza));
        return JsonbBuilder.create().toJson(buono);
    }
}
