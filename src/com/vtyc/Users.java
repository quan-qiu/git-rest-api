package com.vtyc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.sql.*;
import java.util.ArrayList;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Path("/users")
public class Users {
    @GET
    @Produces("application/json")
    @Path("/mysql")
    public String getClichedMessage(){
        String s_user = "";
        Connection connection = null;
        try {
            InitialContext ctx = new InitialContext();

            //The JDBC Data source that we just created

            DataSource ds = (DataSource) ctx.lookup("jdbc/mysql");

            connection = ds.getConnection();

            if (connection == null){
                throw new SQLException("Error establishing connection!");
            }

            String sql = "SELECT id, log_name, name, password, role FROM user where id=1";

            Statement stat = connection.createStatement();
            ResultSet rs = stat.executeQuery(sql);

            while (rs.next()) {
                ObjectMapper mapper = new ObjectMapper();
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setLog_name(rs.getString("log_name"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));

                s_user = mapper.writeValueAsString(user);
                System.out.println("s_user : " + s_user);

            }


        }catch(SQLException ex){
            ex.printStackTrace();
        }catch(NamingException nex){
            nex.printStackTrace();
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }


        return s_user;
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/mssql")
    public String getdata() {
        String connectionUrl = "jdbc:sqlserver://172.17.50.16\\tyc2008;databaseName=damao;" +
                "user=ars_user;password=Cw320222";

        System.out.println(connectionUrl);

        Connection con = null;
        Statement stmt = null;

        String return_str="";
        try {
            /*InitialContext initctx = new InitialContext();
            DataSource ds = (DataSource) initctx.lookup("jdbc/sql");

            Connection conn = ds.getConnection();
            String sql = "select top 1 sID from dbo.BaseInfo";

            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql);*/

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(connectionUrl);

            stmt = con.createStatement();
            String SQL = "SELECT TOP 10 * FROM dbo.V_HRTOCB";

            ResultSet rs = stmt.executeQuery(SQL);

            // Iterate through the data in the result set and display it.
            while (rs.next()) {
                return_str +=rs.getString(6);
                System.out.println(rs.getString(4) + " " + rs.getString(6));
            }

            /*while (con!= null) {
                //s_user = rs.getString("sid");

                //System.out.println("---------------  s_user : " + s_user);
                System.out.println("============== success!");
            }*/
        }catch(SQLException ex){
            ex.printStackTrace();
        }catch(ClassNotFoundException ex){
            ex.printStackTrace();
        }finally {
            try {
                con.close();
            }catch (SQLException ex){}
        }
        return return_str;
    }
}
