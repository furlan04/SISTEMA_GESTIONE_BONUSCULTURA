package it.unimib.sd2025;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.json.bind.JsonbBuilder;

@Path("buono")
public class BuonoResource {
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3030;

    private String sendDatabaseCommand(String command) throws IOException {
        try (Socket socket = new Socket(DB_HOST, DB_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(command);
            return in.readLine();
        }
    }

    
}