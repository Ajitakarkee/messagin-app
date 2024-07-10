package com.ajitakarki.messagingapp;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.websocket.*;
import java.net.URI;

public class ChatController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    private static final String SERVER_URL = "ws://localhost:8080/messaging/chat"; // WebSocket server URL

    private Session session;

    @FXML
    private void initialize() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(new ChatClientEndpoint(this), URI.create(SERVER_URL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                appendMessage("You: " + message);
                messageField.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void appendMessage(String message) {
        chatArea.appendText(message + "\n");
    }

    @ClientEndpoint
    public class ChatClientEndpoint {

        private ChatController controller;

        public ChatClientEndpoint(ChatController controller) {
            this.controller = controller;
        }

        @OnMessage
        public void onMessage(String message, Session session) {
            // Process received message
            controller.appendMessage("Friend: " + message);
        }
    }

}
