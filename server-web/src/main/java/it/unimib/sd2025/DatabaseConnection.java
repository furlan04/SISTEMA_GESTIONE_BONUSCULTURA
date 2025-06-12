package it.unimib.sd2025;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DatabaseConnection {
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3030;

    public static String sendDatabaseCommand(String command) throws IOException {
        try (Socket socket = new Socket(DB_HOST, DB_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);
            return in.readLine();
        }
    }
}
