package com.vtyc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/hr")
public class HrQuery {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/listJobPositionDetail")
    public String listJobPositionDetail(){

        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        String status = "false";
        String msg = "Not found";


        Connection conn = HrConnect.getConnection();

        String sql = "SELECT distinct job_position_detail, job_position " +
                "FROM V_HRTOCB";

        try{
            PreparedStatement pre_stmt = conn.prepareStatement(sql);
            ResultSet rs = pre_stmt.executeQuery();

            data = Utility.convertResultSetToJson(rs);

            if(data.size() > 0){
                status = "true";
                msg = "Data fetched successfully";
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }finally {
            HrConnect.closeConnection();
        }

        result.put("status", status);
        result.put("msg", msg);

        result.put("data", data);

        return result.toJSONString();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getEmployeeInfo/{job_position_detail}")
    public String getEmployeeInfo(@PathParam("job_position_detail") String job_position_detail){

        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        String status = "false";
        String msg = "Not found";


        Connection conn = HrConnect.getConnection();

        String sql = "SELECT employee_id, employee_name, management_level " +
                "FROM V_HRTOCB " +
                "WHERE job_position_detail LIKE ?";

        try{
            PreparedStatement pre_stmt = conn.prepareStatement(sql);
            pre_stmt.setString(1, job_position_detail);
            ResultSet rs = pre_stmt.executeQuery();

            data = Utility.convertResultSetToJson(rs);

            if(data.size() > 0){
                status = "true";
                msg = "Data fetched successfully";
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }finally {
            HrConnect.closeConnection();
        }

        result.put("status", status);
        result.put("msg", msg);

        result.put("data", data);

        return result.toJSONString();

    }


}
