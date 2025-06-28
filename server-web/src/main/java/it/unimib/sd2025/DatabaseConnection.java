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
    private static DatabaseConnection _instance;

    public synchronized static DatabaseConnection getInstance() throws RuntimeException {
        if (_instance == null) {
            try {
                _instance = new DatabaseConnection();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create DatabaseConnection instance: " + e.getMessage());
            }
        }
        return _instance;
    }

    private DatabaseConnection() throws IOException {
        this.socket = new Socket(DB_HOST, DB_PORT);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public synchronized String sendDatabaseCommand(String command) throws IOException {
        out.println(command);
        return in.readLine();
    }
}