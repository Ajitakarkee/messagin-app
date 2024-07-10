package com.ajitakarki.messagingapp;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Timestamp;
import javax.net.ssl.HttpsURLConnection;

public class ChatServer {
    private static final int PORT = 5002;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try {
            // Start server socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started. Waiting for clients...");

            // Accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read username from client
            String username = input.readLine();
            System.out.println("Username received: " + username);

            // Add client to the map
            clients.put(username, output);

            // Start a thread to handle client messages
            Thread clientThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = input.readLine()) != null) {
                        System.out.println("Received message from " + username + ": " + message);

                        // Send message to the servlet
                        sendToServlet(username, message);

                        // Broadcast message to all clients
                        broadcast(username, message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // Remove client from map when disconnected
                    clients.remove(username);
                    System.out.println(username + " disconnected.");
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            clientThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void broadcast(String username, String message) {
        String timestampedMessage = getCurrentTimestamp() + " - " + username + ": " + message;
        System.out.println("Broadcasting message: " + timestampedMessage);
        for (PrintWriter writer : clients.values()) {
            writer.println(timestampedMessage);
        }
    }

    private static void sendToServlet(String sender, String message) {
        try {
            URL url = new URL("http://localhost:8080/messaging/storeMessage");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String urlParameters = "sender=" + URLEncoder.encode(sender, "UTF-8") +
                    "&message=" + URLEncoder.encode(message, "UTF-8");
            System.out.println(urlParameters);
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                out.writeBytes(urlParameters);
                out.flush();
            }

            int responseCode = connection.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("POST request did not work.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
