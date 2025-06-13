package it.unimib.sd2025;

import java.io.IOException;

import jakarta.json.bind.JsonbBuilder;

public class UtenteRepository extends DatabaseConnection {

    public UtenteRepository() {
        super();
    }

    public String existsUtente(String cf) throws IOException {
        if(cf == null || cf.isEmpty()) {
            return "ERROR: Codice Fiscale cannot be null or empty";
        }
        String response = sendDatabaseCommand("exists utente:" + cf);
        if(response.startsWith("ERROR")) {
            return "ERROR: User with Codice Fiscale " + cf + " does not exist";
        }
        return response;
    }

    public String createUtente(String cf, String nome, String cognome, String email) throws IOException {
        if(cf == null || cf.isEmpty() || nome == null || nome.isEmpty() || cognome == null || cognome.isEmpty() || email == null || email.isEmpty()) {
            return "ERROR: Codice Fiscale, Nome, Cognome and Email cannot be null or empty";
        }
        try{
            if(!existsUtente(cf).equals("false")) {
                return "ERROR: User with Codice Fiscale " + cf + " already exists";
            }
            sendDatabaseCommand("set utente:" + cf + ":nome " + nome);
            sendDatabaseCommand("set utente:" + cf + ":cognome " + cognome);
            sendDatabaseCommand("set utente:" + cf + ":email " + email);
            sendDatabaseCommand("set utente:" + cf + ":buoni");
        } catch (IOException e) {
            return "ERROR: Failed to create user with Codice Fiscale " + cf + ". " + e.getMessage();
        }
        return "SUCCESS";
    }

    public String getUtente(String cf) throws IOException {
        if (cf == null || cf.isEmpty()) {
            return "ERROR: Codice Fiscale cannot be null or empty";
        }

        if (!existsUtente(cf).equals("true")) {
            return "ERROR: User with Codice Fiscale " + cf + " does not exist";
        }

        String nome = sendDatabaseCommand("get utente:" + cf + ":nome");
        String cognome = sendDatabaseCommand("get utente:" + cf + ":cognome");
        String email = sendDatabaseCommand("get utente:" + cf + ":email");
        

        if (nome.startsWith("ERROR") || cognome.startsWith("ERROR") || email.startsWith("ERROR")) {
            return "ERROR: User with Codice Fiscale " + cf + " does not exist";
        }

        Utente utente = new Utente(nome, cognome, email, cf);
        return JsonbBuilder.create().toJson(utente);
    }

    public String deleteUtente(String cf) throws IOException {
        if(cf == null || cf.isEmpty()) {
            return "ERROR: Codice Fiscale cannot be null or empty";
        }
        if(existsUtente(cf).equals("false")) {
            return "ERROR: User with Codice Fiscale " + cf + " does not exist";
        }
        sendDatabaseCommand("delete utente:" + cf + ":nome");
        sendDatabaseCommand("delete utente:" + cf + ":cognome");
        sendDatabaseCommand("delete utente:" + cf + ":email");
        String buoni = sendDatabaseCommand("get utente:" + cf + ":buoni");
        return buoni;
    }
    public String addBuonoUtente(String cf, String buonoId) throws IOException {
        if(cf == null || cf.isEmpty() || buonoId == null || buonoId.isEmpty()) {
            return "ERROR: Codice Fiscale and Buono ID cannot be null or empty";
        }
        if(!existsUtente(cf).equals("true")) {
            return "ERROR: User with Codice Fiscale " + cf + " does not exist";
        }
        String buoni = sendDatabaseCommand("get utente:" + cf + ":buoni");
        if(buoni.startsWith("ERROR")) {
            return "ERROR: Failed to retrieve user's buoni";
        }
        if(buoni.contains(buonoId)) {
            return "ERROR: Buono with ID " + buonoId + " already exists for user with Codice Fiscale " + cf;
        }
        sendDatabaseCommand("set utente:" + cf + ":buoni " + buoni + ":" + buonoId);
        return "SUCCESS";
    }
    
    public String getBuoniUtente(String cf) throws IOException {
        if(cf == null || cf.isEmpty()) {
            return "ERROR: Codice Fiscale cannot be null or empty";
        }
        if(!existsUtente(cf).equals("true")) {
            return "ERROR: User with Codice Fiscale " + cf + " does not exist";
        }
        String buoni = sendDatabaseCommand("get utente:" + cf + ":buoni");
        if(buoni.startsWith("ERROR")) {
            return "ERROR: Failed to retrieve user's buoni";
        }
        return buoni;
    }

    public String removeBuonoUtente (String cf, String id) throws IOException {
        if(cf == null || cf.isEmpty() || id == null || id.isEmpty()) {
            return "ERROR: Codice Fiscale and Buono ID cannot be null or empty";
        }
        if(!existsUtente(cf).equals("true")) {
            return "ERROR: User with Codice Fiscale " + cf + " does not exist";
        }
        String buoni = sendDatabaseCommand("get utente:" + cf + ":buoni");
        if(buoni.startsWith("ERROR")) {
            return "ERROR: Failed to retrieve user's buoni";
        }
        if(!buoni.contains(id)) {
            return "ERROR: Buono with ID " + id + " does not exist for user with Codice Fiscale " + cf;
        }
        String newBuoni = buoni.replace(":" + id, "").replace(id + ":", "");
        sendDatabaseCommand("set utente:" + cf + ":buoni " + newBuoni);
        return "SUCCESS";
    }
}
