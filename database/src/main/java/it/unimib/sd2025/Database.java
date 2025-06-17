package it.unimib.sd2025;

import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Database {
    private Map<String, String> data;
    private static final String INIT_FILE = "./initData.ini";

    private void seedDatabase() {
        Path path = Paths.get(INIT_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (line.trim().startsWith(";") || line.trim().isEmpty()) {
                    continue;
                }

                // Split the line into key and value
                String[] parts = line.split(" ", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    data.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading init file: " + e.getMessage());
        }
    }

    public Database() {
        this.data = new HashMap<String, String>();
        seedDatabase();

    }

    /**
     * Esegue un'azione basata sul comando fornito.
     * 
     * @param command Il comando da eseguire, ad esempio "get key", "set key value",
     *                "delete key".
     * @return Il risultato dell'azione eseguita.
     * @throws Exception Se il comando non è valido o se si verifica un errore
     *                   durante l'esecuzione.
     */
    public synchronized String action(String command) throws Exception {
        if (command == null || command.isEmpty()) {
            throw new Exception("Command cannot be null or empty");
        }
        // Split the command into method, key, and value (max 3 parts)
        String[] parts = command.split(" ", 3);
        String method = parts[0];
        String key = parts[1];
        String value = "";
        if (parts.length > 2) {
            value = parts[2];
        }
        switch (method.toLowerCase()) {
            case "exists":
                return exists(key);
            case "getAllKeys":
                return getKeys();
            case "get":
                return get(key);
            case "set":
                return set(key, value);
            case "delete":
                return delete(key);
            default:
                throw new Exception("command not found!");
        }
    }

    public String getKeys() {
        StringBuilder keys = new StringBuilder();
        for (String key : data.keySet()) {
            keys.append(key).append(";");
        }
        return keys.toString().trim(); // Remove trailing newline
    }

    public String get(String key) throws Exception {

        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            throw new Exception("Key not found: " + key);
        }
    }

    public String set(String key, String value) {
        if (data.containsKey(key)) {
            String oldValue = data.get(key);
            data.put(key, value);
            return oldValue;
        } else {
            data.put(key, value);
            return "true";
        }
    }

    public String delete(String key) throws Exception {
        if (data.containsKey(key)) {
            String oldValue = data.get(key);
            data.remove(key);
            return oldValue;
        } else {
            throw new Exception("Key not found: " + key);
        }
    }

    public String exists(String partial_key) {
        if (partial_key == null || partial_key.isEmpty()) {
            return "false";
        }
        for (String key : data.keySet()) {
            if (key.startsWith(partial_key)) {
                return "true";
            }
        }
        return "false";
    }
}