package it.unimib.sd2025.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.sd2025.DatabaseConnection;
import it.unimib.sd2025.Model.SaldoRimasto;
import it.unimib.sd2025.Model.Utente;

public class UtenteRepository implements Repository<Utente> {
    private DatabaseConnection dbConnection;
    public UtenteRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        
    }
    @Override
    public boolean exists(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale cannot be null or empty");
        }
        String response = dbConnection.sendDatabaseCommand("exists utente:" + cf);
        if (response.startsWith("ERROR")) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }
        return "true".equals(response);
    }
    @Override
    public Utente create(Utente utente) throws IOException {
        if (utente.getCodiceFiscale() == null || utente.getCodiceFiscale().isEmpty()
                || utente.getNome() == null || utente.getNome().isEmpty()
                || utente.getCognome() == null || utente.getCognome().isEmpty()
                || utente.getEmail() == null || utente.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale, Nome, Cognome, and Email cannot be null or empty");
        }
        try {
            if (exists(utente.getCodiceFiscale())) {
                throw new IOException("User with Codice Fiscale " + utente.getCodiceFiscale() + " already exists");
            }
            dbConnection.sendDatabaseCommand("set utente:" + utente.getCodiceFiscale() + ":nome " + utente.getNome());
            dbConnection.sendDatabaseCommand("set utente:" + utente.getCodiceFiscale() + ":cognome " + utente.getCognome());
            dbConnection.sendDatabaseCommand("set utente:" + utente.getCodiceFiscale() + ":email " + utente.getEmail());
            dbConnection.sendDatabaseCommand("set utente:" + utente.getCodiceFiscale() + ":buoni ");

        } catch (IOException e) {
            throw new IOException(
                    "Failed to create user with Codice Fiscale " + utente.getCodiceFiscale() + ". " + e.getMessage());
        }
        return utente;
    }
    @Override
    public Utente get(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale cannot be null or empty");
        }

        if (!exists(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }

        String nome = dbConnection.sendDatabaseCommand("get utente:" + cf + ":nome");
        String cognome = dbConnection.sendDatabaseCommand("get utente:" + cf + ":cognome");
        String email = dbConnection.sendDatabaseCommand("get utente:" + cf + ":email");

        if (nome.startsWith("ERROR") || cognome.startsWith("ERROR") || email.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user information for Codice Fiscale " + cf);
        }

        return new Utente(nome, cognome, email, cf);
    }

    public String addBuonoUtente(String cf, String buonoId) throws IOException {
        if (cf == null || cf.isEmpty() || buonoId == null || buonoId.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale and Buono ID cannot be null or empty");
        }
        if (!exists(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }
        String buoni = dbConnection.sendDatabaseCommand("get utente:" + cf + ":buoni");
        if (buoni.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user's buoni for Codice Fiscale " + cf);
        }
        if (buoni == null || buoni.isEmpty()) {
            buoni = buonoId;
        } else {
            for (String id : buoni.split(":")) {
                if (id.equals(buonoId)) {
                    throw new IOException(
                            "Buono with ID " + buonoId + " already exists for user with Codice Fiscale " + cf);
                }
            }
            buoni = buoni + ":" + buonoId;
        }
        String buoniAgg = dbConnection.sendDatabaseCommand("set utente:" + cf + ":buoni " + buoni);
        return buoniAgg;
    }

    public String getBuoniUtente(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale cannot be null or empty");
        }
        if (!exists(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }
        String buoni = dbConnection.sendDatabaseCommand("get utente:" + cf + ":buoni");
        if (buoni.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user's buoni for Codice Fiscale " + cf);
        }
        return buoni;
    }

    public String removeBuonoUtente(String cf, String id) throws IOException {
        if (cf == null || cf.isEmpty() || id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale and Buono ID cannot be null or empty");
        }

        if (!exists(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }

        String buoni = dbConnection.sendDatabaseCommand("get utente:" + cf + ":buoni");
        if (buoni.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user's buoni for Codice Fiscale " + cf);
        }

        // Split and clean all IDs
        String[] buoniArray = buoni.split(":");
        List<String> updatedBuoniList = new ArrayList<>();
        boolean found = false;

        for (String buono : buoniArray) {
            String cleanBuono = buono.trim();
            if (!cleanBuono.isEmpty()) {
                if (cleanBuono.equals(id)) {
                    found = true; // Match found, skip this one
                } else {
                    updatedBuoniList.add(cleanBuono);
                }
            }
        }

        if (!found) {
            throw new IOException("Buono with ID " + id + " not found for user with Codice Fiscale " + cf);
        }

        String newBuoni = String.join(":", updatedBuoniList);

        // Set or delete the field depending on remaining content
        if (newBuoni.isEmpty()) {
            dbConnection.sendDatabaseCommand("del utente:" + cf + ":buoni");
        } else {
            dbConnection.sendDatabaseCommand("set utente:" + cf + ":buoni " + newBuoni);
        }

        return newBuoni;
    }

    public SaldoRimasto getSaldoRimastoUtente(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale cannot be null or empty");
        }
        if (!exists(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }
        String buoni = dbConnection.sendDatabaseCommand("get utente:" + cf + ":buoni");
        if (buoni.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user's buoni for Codice Fiscale " + cf);
        }
        double saldo_consumato = 0.0;
        double saldo_non_consumato = 0.0;
        for (String id : buoni.split(":")) {
            if (!(id == null || id.isEmpty())) {
                String saldo = dbConnection.sendDatabaseCommand("get buono:" + id + ":valore");
                if (saldo.startsWith("ERROR")) {
                    throw new IOException("Failed to retrieve value for Buono with ID " + id);
                }
                if(dbConnection.sendDatabaseCommand("get buono:" + id + ":dataConsumo").isEmpty()) {
                    saldo_non_consumato += Double.parseDouble(saldo);
                } else {
                    saldo_consumato += Double.parseDouble(saldo);
                }
            }
        }
        return new SaldoRimasto(500 - saldo_consumato - saldo_non_consumato, saldo_consumato, saldo_non_consumato);
    }
    
}
