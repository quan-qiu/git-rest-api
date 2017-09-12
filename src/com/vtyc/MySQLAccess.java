package com.vtyc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class MySQLAccess {
    private static Connection conn = null;
    private static String URL = "jdbc:mysql://localhost:3306/vtyc_web?autoReconnect=true&useSSL=false";
    private static String USER = "root";
    private static String PASSWORD = "1q2w3e4";

    public static Connection getConn(){

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conn != null) {
                System.out.println("Connected to the database test1");
            }
        }catch(SQLException ex){
            System.out.println("An error occurred. Maybe user/password is invalid");
            ex.printStackTrace();
        }
        return conn;
    }

    public void closeConn(){

        if (conn != null){
            try {
                conn.close();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }

}
