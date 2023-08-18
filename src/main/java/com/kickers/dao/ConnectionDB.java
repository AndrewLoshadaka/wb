package com.kickers.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            String user = "andrew";
            String password = "andrew5525613";

            String url = "jdbc:postgresql://localhost:5432/keys_wb";

            connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (SQLException e){

        }
        return null;
    }
}
