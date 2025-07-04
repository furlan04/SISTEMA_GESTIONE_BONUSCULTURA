package it.unimib.sd2025;

import java.net.*;
import java.io.*;

/**
 * Classe principale in cui parte il database.
 */
public class Main {
    /**
     * Porta di ascolto.
     */
    public static final int PORT = 3030;
    // database
    private static Database database = new Database();

    /**
     * Avvia il database e l'ascolto di nuove connessioni.
     */
    public static void startServer() throws IOException {
        var server = new ServerSocket(PORT);

        System.out.println("Database listening at localhost:" + PORT);

        try {
            while (true)
                new Handler(server.accept()).start();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            server.close();
        }
    }

    /**
     * Handler di una connessione del client.
     */
    private static class Handler extends Thread {
        private Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        private boolean validAction(String action) {
            return action.startsWith("get ") || action.startsWith("set ") || action.startsWith("delete ")
                    || action.startsWith("exists ") || action.startsWith("getallkeys");
        }

        public void run() {
            try {
                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.isEmpty()) {
                        out.println("ERROR: Empty command");
                        continue;
                    }
                    if (validAction(inputLine)) {
                        try {
                            String result = database.action(inputLine);
                            out.println(result);
                        } catch (Exception e) {
                            out.println("ERROR: " + e.getMessage());
                        }
                    } else if ("END".equals(inputLine)) {
                        out.println("bye");
                        break;
                    }

                }

                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * Metodo principale di avvio del database.
     *
     * @param args argomenti passati a riga di comando.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        startServer();
    }
}
