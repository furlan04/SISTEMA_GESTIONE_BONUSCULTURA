package it.unimib.sd2025;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DatabaseConnection implements AutoCloseable {
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3030;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public DatabaseConnection() throws IOException {
        this.socket = new Socket(DB_HOST, DB_PORT);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String sendDatabaseCommand(String command) throws IOException {
        out.println(command);
        return in.readLine();
    }

    @Override
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("ERROR: Failed to close database connection");
            e.printStackTrace();
        }
    }
}