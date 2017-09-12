package com.vtyc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HrConnect {
    private static Connection conn = null;

    public static Connection getConnection(){
        String connectionUrl = "jdbc:sqlserver://172.17.50.16\\tyc2008;databaseName=damao;" +
                "user=ars_user;password=Cw320222";

        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(connectionUrl);
        }catch(Exception e){
            e.printStackTrace();
        }

        return conn;
    }

    public static void closeConnection(){
        if (conn != null){
            try {
                conn.close();
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

}
