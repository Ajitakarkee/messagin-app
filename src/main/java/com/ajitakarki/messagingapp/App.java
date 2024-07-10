package com.ajitakarki.messagingapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Prompt user for username
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Username");
        dialog.setHeaderText("Please enter your username:");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            ChatClient chatClient = new ChatClient();
            chatClient.start(primaryStage, username);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
