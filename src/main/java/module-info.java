module com.ajitakarki.messagingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires servlet.api;
    requires java.sql;
    requires org.json;
    requires websocket.api;
    opens com.ajitakarki.messagingapp to javafx.fxml;
    exports com.ajitakarki.messagingapp;
}