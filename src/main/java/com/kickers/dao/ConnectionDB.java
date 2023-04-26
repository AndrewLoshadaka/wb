package com.kickers.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            String user = "andrew";
            String password = "1925";
            String url = "jdbc:mysql://localhost:3306/_schema";
            connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (SQLException e){

        }
        return null;
    }
}
