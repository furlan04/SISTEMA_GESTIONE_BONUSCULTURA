package it.unimib.sd2025;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DatabaseConnection {
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3030;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public DatabaseConnection() {
        // Constructor can be used for initialization if needed
        try (Socket socket = new Socket(DB_HOST, DB_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            this.socket = socket;
            this.out = out;
            this.in = in;
        } catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public String sendDatabaseCommand(String command) throws IOException {
        out.println(command);
        String response = in.readLine();
        return response != null ? response : "No response from database";
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
