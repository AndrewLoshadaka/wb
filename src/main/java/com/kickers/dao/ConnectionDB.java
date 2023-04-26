package com.kickers.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            String user = "newgen";
            String password = "OleG2170458";
            String url = "jdbc:mysql://192.168.208.137:3306/wbstat";
            connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (SQLException e){

        }
        return null;
    }
}
