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

    public boolean existsBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException( "ERROR: ID cannot be null or empty");
        }
        String response = sendDatabaseCommand("exists buono:" + id);
        if (response.startsWith("ERROR")) {
            throw new IOException( "ERROR: Buono with ID " + id + " does not exist");
        }
        if (response.equals("true")) {
            return true;
        } else if (response.equals("false")) {
            return false;
        } else {
            throw new IOException("ERROR: Unexpected response from database for Buono with ID " + id);
        }
    }

    public Buono createBuono(Buono buono) throws Exception, IllegalArgumentException, IOException
            throws IOException {
        if (buono.id == null || buono.id.isEmpty() || buono.tipologia == null || buono.tipologia.isEmpty() || buono.dataCreazione == null || buono.dataCreazione.toString().isEmpty()) {
            throw new IllegalArgumentException( "ERROR: ID, Tipologia, Data Creazione cannot be null or empty");
        }
        if (existsBuono(buono.id)) {
            throw new IOException("ERROR: Buono with ID " + buono.id + " already exists");
        }
        try {
            sendDatabaseCommand("set buono:" + buono.id + ":valore " + buono.valore);
            sendDatabaseCommand("set buono:" + buono.id + ":tipologia " + buono.tipologia);
            sendDatabaseCommand("set buono:" + buono.id + ":dataCreazione " + buono.dataCreazione);
            sendDatabaseCommand("set buono:" + buono.id + ":dataConsumo ");
        } catch (IOException e) {
            throw new Exception("ERROR: Failed to create Buono with ID " + buono.id + ". " + e.getMessage());
        }
        return buono;
    }

    public Buono getBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ERROR: ID cannot be null or empty");
        }

        if (!"true".equals(existsBuono(id))) {
            throw new Exception("ERROR: Buono with ID " + id + " does not exist");
        }

        String valoreStr = sendDatabaseCommand("get buono:" + id + ":valore");
        String tipologia = sendDatabaseCommand("get buono:" + id + ":tipologia");
        String dataCreazioneStr = sendDatabaseCommand("get buono:" + id + ":dataCreazione");
        String dataScadenzaStr = sendDatabaseCommand("get buono:" + id + ":dataConsumo");

        // Controlla errori nella risposta dal DB
        if (valoreStr.startsWith("ERROR") || tipologia.startsWith("ERROR") ||
                dataCreazioneStr.startsWith("ERROR") || dataScadenzaStr.startsWith("ERROR")) {
            throw new IOException("ERROR: Buono with ID " + id + " could not be retrieved");
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

            return buono;
        } catch (IllegalArgumentException e) {
            throw new IOException("ERROR: Invalid data format for Buono with ID " + id);
        }
    }

    public Buono deleteBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ERROR: ID cannot be null or empty");
        }
        if (existsBuono(id)) {
            throw new Exception("ERROR: Buono with ID " + id + " does not exist");
        }
        if(!sendDatabaseCommand("get buono:" + id + ":dataConsumo").equals("")) {
            throw new Exception("ERROR: Buono with ID " + id + " has been consumed and cannot be deleted");
        }
        String valore = sendDatabaseCommand("delete buono:" + id + ":valore");
        String tipologia = sendDatabaseCommand("delete buono:" + id + ":tipologia");
        String dataCreazione = sendDatabaseCommand("delete buono:" + id + ":dataCreazione");
        sendDatabaseCommand("delete buono:" + id + ":dataConsumo");
        Buono buono = new Buono(id, Double.parseDouble(valore), tipologia, Date.valueOf(dataCreazione),
                null);
        
        return buono;
    }

    public Buono consumaBuono(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ERROR: ID cannot be null or empty");
        }
        if (existsBuono(id)) {
            throw new Exception("ERROR: Buono with ID " + id + " does not exist");
        }
        if(!sendDatabaseCommand("get buono:" + id + ":dataConsumo").equals("")) {
            throw new Exception("ERROR: Buono with ID " + id + " has already been consumed");
        }
        String dataConsumo = new Date(System.currentTimeMillis()).toString();
        sendDatabaseCommand("set buono:" + id + ":dataConsumo " + dataConsumo);
        Buono buono = getBuono(id);
        if (buono == null) {
            throw new IOException("ERROR: Buono with ID " + id + " could not be retrieved after consumption");
        }
        return buono;
    }
}
