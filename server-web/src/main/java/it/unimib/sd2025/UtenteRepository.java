package it.unimib.sd2025;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UtenteRepository extends DatabaseConnection {

    public UtenteRepository() {
        super();
    }

    public boolean existsUtente(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale cannot be null or empty");
        }
        String response = sendDatabaseCommand("exists utente:" + cf);
        if (response.startsWith("ERROR")) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }
        return "true".equals(response);
    }

    public Utente createUtente(Utente utente) throws IOException {
        if (utente.codiceFiscale == null || utente.codiceFiscale.isEmpty() || utente.nome == null || utente.nome.isEmpty() || utente.cognome == null || utente.cognome.isEmpty()
                || utente.email == null || utente.email.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale, Nome, Cognome, and Email cannot be null or empty");
        }
        try {
            if (!existsUtente(utente.codiceFiscale)) {
                throw new IOException("User with Codice Fiscale " + utente.codiceFiscale + " already exists");
            }
            sendDatabaseCommand("set utente:" + utente.codiceFiscale + ":nome " + utente.nome);
            sendDatabaseCommand("set utente:" + utente.codiceFiscale + ":cognome " + utente.cognome);
            sendDatabaseCommand("set utente:" + utente.codiceFiscale + ":email " + utente.email);
            sendDatabaseCommand("set utente:" + utente.codiceFiscale + ":buoni");

        } catch (IOException e) {
            throw new IOException("Failed to create user with Codice Fiscale " + utente.codiceFiscale + ". " + e.getMessage());
        }
        return utente;
    }

    public Utente getUtente(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale cannot be null or empty");
        }

        if (!existsUtente(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }

        String nome = sendDatabaseCommand("get utente:" + cf + ":nome");
        String cognome = sendDatabaseCommand("get utente:" + cf + ":cognome");
        String email = sendDatabaseCommand("get utente:" + cf + ":email");

        if (nome.startsWith("ERROR") || cognome.startsWith("ERROR") || email.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user information for Codice Fiscale " + cf);
        }

        return new Utente(nome, cognome, email, cf);
    }

    public String addBuonoUtente(String cf, String buonoId) throws IOException {
        if (cf == null || cf.isEmpty() || buonoId == null || buonoId.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale and Buono ID cannot be null or empty");
        }
        if (!existsUtente(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }
        String buoni = sendDatabaseCommand("get utente:" + cf + ":buoni");
        if (buoni.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user's buoni for Codice Fiscale " + cf);
        }
        for (String id : buoni.split(":")) {
            if (id.equals(buonoId)) {
               throw new IOException("Buono with ID " + buonoId + " already exists for user with Codice Fiscale " + cf);
            }
        }
        sendDatabaseCommand("set utente:" + cf + ":buoni " + buoni + ":" + buonoId);
        return "SUCCESS";
    }

    public String getBuoniUtente(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale cannot be null or empty");
        }
        if (!existsUtente(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }
        String buoni = sendDatabaseCommand("get utente:" + cf + ":buoni");
        if (buoni.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user's buoni for Codice Fiscale " + cf);
        }
        return buoni;
    }

    public String removeBuonoUtente(String cf, String id) throws IOException {
        if (cf == null || cf.isEmpty() || id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale and Buono ID cannot be null or empty");
        }

        if (!existsUtente(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }

        String buoni = sendDatabaseCommand("get utente:" + cf + ":buoni");
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
            sendDatabaseCommand("del utente:" + cf + ":buoni");
        } else {
            sendDatabaseCommand("set utente:" + cf + ":buoni " + newBuoni);
        }

        return newBuoni;
    }

    public SaldoRimasto getSaldoRimastoUtente(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            throw new IllegalArgumentException("Codice Fiscale cannot be null or empty");
        }
        if (!existsUtente(cf)) {
            throw new IOException("User with Codice Fiscale " + cf + " does not exist");
        }
        String buoni = sendDatabaseCommand("get utente:" + cf + ":buoni");
        if (buoni.startsWith("ERROR")) {
            throw new IOException("Failed to retrieve user's buoni for Codice Fiscale " + cf);
        }
        double saldoRimasto = 0.0;
        for (String id : buoni.split(":")) {
            String saldo = sendDatabaseCommand("get buono:" + id + ":valore");
            if (saldo.startsWith("ERROR")) {
                throw new IOException("Failed to retrieve value for Buono with ID " + id);
            }
            saldoRimasto += Double.parseDouble(saldo);
        }
        return new SaldoRimasto(500 - saldoRimasto);
    }
}
