package com.vtyc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;

public class VtycDao {

    public ArrayList getUsers(){
        ArrayList result = new ArrayList();
        Connection conn = MySQLAccess.getConn();
        try {
            Statement stmt = conn.createStatement();
            String sql;

            sql = "SELECT id, log_name, name, password, role FROM user";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()){
                ObjectMapper mapper = new ObjectMapper();
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setLog_name(rs.getString("log_name"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));

                String s_user = mapper.writeValueAsString(user);

                result.add(s_user);

            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args){
        VtycDao vtycDao = new VtycDao();
        ArrayList results = vtycDao.getUsers();
        System.out.println(results.toString());
    }
}
