package com.vtyc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class DBConnect {
    private static Connection conn = null;
    private static Statement stat = null;

    public static Connection getConnection(){
        try {
            InitialContext ctx = new InitialContext();

            //The JDBC Data source that we just created

            DataSource ds = (DataSource) ctx.lookup("jdbc/mysql");

            conn = ds.getConnection();
        }catch (NamingException nex){
            nex.printStackTrace();
        }catch (SQLException sqlex){
            sqlex.printStackTrace();
        }

        return conn;
    }

    public static Statement getStatement(){
        try {
            stat = conn.createStatement();
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
        return stat;
    }

    public static void closeAll(){
        try{
            conn.close();
            stat.close();
        }catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
    }
}
