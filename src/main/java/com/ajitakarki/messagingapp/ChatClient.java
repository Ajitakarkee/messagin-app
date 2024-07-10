package com.ajitakarki.messagingapp;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClient {
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private String username;

    private TextArea messageArea;
    private TextField inputField;

    public void start(Stage primaryStage, String username) {
        this.username = username;

        VBox root = new VBox();
        messageArea = new TextArea();
        messageArea.setEditable(false);
        inputField = new TextField();
        Button sendButton = new Button("Send");

        sendButton.setOnAction(event -> sendMessage());

        root.getChildren().addAll(messageArea, inputField, sendButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Client - " + username);
        primaryStage.setOnCloseRequest(event -> disconnectFromServer());
        primaryStage.show();

        connectToServer();

        Thread receiveThread = new Thread(this::receiveMessages);
        receiveThread.start();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5002);
            System.out.println("Connected to server.");

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Send username to server
            output.write(username + "\n");
            output.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnectFromServer() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                output.write( message + "\n");
                output.flush();
                inputField.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            while (true) {
                String message = input.readLine();
                if (message == null)
                    break;
                Platform.runLater(() -> messageArea.appendText(message + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
