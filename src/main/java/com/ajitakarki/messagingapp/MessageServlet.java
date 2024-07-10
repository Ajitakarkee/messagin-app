package com.ajitakarki.messagingapp;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private PreparedStatement insertStatement;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            // Initialize database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/chatapp", "root", "");
            // Prepare statement for inserting messages
            insertStatement = connection.prepareStatement("INSERT INTO messages (sender, content, timestamp) VALUES (?, ?, ?)");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new ServletException("Unable to initialize database connection", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sender = request.getParameter("sender");
        String message = request.getParameter("message");

        try {
            // Get current timestamp
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            // Insert message into database
            insertStatement.setString(1, sender);
            insertStatement.setString(2, message);
            insertStatement.setTimestamp(3, timestamp);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());

            throw new ServletException("Error inserting message into database", e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (insertStatement != null) {
                insertStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
