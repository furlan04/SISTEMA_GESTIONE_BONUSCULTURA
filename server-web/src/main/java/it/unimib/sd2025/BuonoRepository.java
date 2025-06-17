package it.unimib.sd2025;

import java.io.IOException;
import java.sql.Date;

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

    public Buono createBuono(Buono buono) throws Exception, IllegalArgumentException, IOException{
        if (buono.getId() == null || buono.getId().isEmpty() || buono.getTipologia() == null || buono.getTipologia().isEmpty()) {
            throw new IllegalArgumentException( "ERROR: ID, Tipologia, Data Creazione cannot be null or empty");
        }
        Buono new_buono = new Buono(buono.getValore(), buono.getTipologia());
        while (existsBuono(new_buono.getId())) {
            new_buono.setId(String.valueOf(Id.getNextId()));
        }
        try {
            sendDatabaseCommand("set buono:" + buono.getId() + ":valore " + new_buono.getValore());
            sendDatabaseCommand("set buono:" + buono.getId() + ":tipologia " + new_buono.getTipologia());
            sendDatabaseCommand("set buono:" + buono.getId() + ":dataCreazione " + new_buono.getDataCreazione().toString());
            sendDatabaseCommand("set buono:" + buono.getId() + ":dataConsumo ");
        } catch (IOException e) {
            throw new Exception("ERROR: Failed to create Buono with ID " + buono.getId()+ ". " + e.getMessage());
        }
        return buono;
    }

    public Buono getBuono(String id) throws IOException, Exception {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ERROR: ID cannot be null or empty");
        }

        if (!existsBuono(id)) {
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

    public Buono deleteBuono(String id) throws Exception {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ERROR: ID cannot be null or empty");
        }
        if (!existsBuono(id)) {
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

    public Buono consumaBuono(String id) throws Exception {
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

    public Buono updateBuono(String id, Buono updatedBuono) throws Exception {
        if (id == null || id.isEmpty() || updatedBuono == null) {
            throw new IllegalArgumentException("ERROR: ID and updated Buono cannot be null or empty");
        }
        if (!existsBuono(id)) {
            throw new Exception("ERROR: Buono with ID " + id + " does not exist");
        }
        if(!sendDatabaseCommand("get buono:" + id + ":dataConsumo").equals("")) {
            throw new Exception("ERROR: Buono with ID " + id + " has already been consumed and cannot be modified");
        }
        
        sendDatabaseCommand("set buono:" + id + ":valore " + updatedBuono.getValore());
        sendDatabaseCommand("set buono:" + id + ":tipologia " + updatedBuono.getTipologia());
                
        return getBuono(id);
    }
}
