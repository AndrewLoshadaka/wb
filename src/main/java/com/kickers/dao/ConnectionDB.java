package com.kickers.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    public static Connection getConnection() {
        try {
            String user = "newgen";
            String password = "OleG2170458";
            String url = "jdbc:mysql://192.168.208.137:3306/wbstat";

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
