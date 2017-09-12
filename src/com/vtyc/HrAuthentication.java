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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;

@Path("/hr")
public class HrAuthentication {

    private static final String SHIFT_TYPE_DAY = "DAY_SHIFT";
    private static final String SHIFT_TYPE_NIGHT = "NIGHT_SHIFT";
    private static final String SHIFT_TYPE_CZ_DAY_SHIFT = "CZ_DAY_SHIFT"; // 08:00-16:00, 8:00-20:00
    private static final String SHIFT_TYPE_CZ_MFG_NIGHT_SHIFT = "CZ_MFG_NIGHT_SHIFT"; // 20:00-04:00, 20:00-8:00
    private static final String SHIFT_TYPE_CZ_MIDDLE_SHIFT = "CZ_MIDDLE_SHIFT"; // 16:00-00:00,
    private static final String SHIFT_TYPE_CZ_ASSY_DAY_SHIFT = "CZ_ASSY_DAY_SHIFT"; // 08:00-16:30, 8:00-20:00
    private static final String SHIFT_TYPE_CZ_ASSY_NIGHT_SHIFT = "CZ_ASSY_NIGHT_SHIFT"; // 20:00-04:30, 20:00-8:00

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getMyWorkersListFromWC/{tm_user_id}/{work_center}/{cn_shift_name}")
    public String getMyWorkersListFromWC(@PathParam("tm_user_id") String tm_user_id,
                                    @PathParam("work_center") String work_center,
                                    @PathParam("cn_shift_name") String cn_shift_name){

        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        String status = "false";
        String msg = "Not found";


        Connection conn = HrConnect.getConnection();
        String sql = "SELECT " +
                    "employee_id, employee_name " +
                    "FROM V_HRTOCB " +
                    "WHERE " +
                    "(team_leader_day_shift_id LIKE ? OR team_leader_night_shift_id LIKE ?) AND " +
                    "work_center LIKE ? AND " +
                    "management_level LIKE ? AND " +
                    "job_position LIKE ? AND " +
                    "plant_location LIKE ? AND " +
                    "shift LIKE ?";

        try{
            PreparedStatement pre_stmt = conn.prepareStatement(sql);
            pre_stmt.setString(1, tm_user_id);
            pre_stmt.setString(2, tm_user_id);
            pre_stmt.setString(3, work_center);
            pre_stmt.setString(4, "Direct");
            pre_stmt.setString(5, "Worker");
            pre_stmt.setString(6, "CZ");
            pre_stmt.setString(7, cn_shift_name);

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
    @Path("/getMyWorkCenterList/{tm_user_id}/{cn_shift_name}")
    public String getMyWorkCenterList(@PathParam("tm_user_id") String tm_user_id,
                               @PathParam("cn_shift_name") String cn_shift_name){

        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        String status = "false";
        String msg = "Not found";

        Connection conn = HrConnect.getConnection();

        try{
            String sql = "SELECT " +
                    "work_center " +
                    "FROM V_HRTOCB " +
                    "WHERE " +
                    "(team_leader_day_shift_id LIKE ? OR team_leader_night_shift_id LIKE ?) AND " +
                    "management_level LIKE ? AND " +
                    "job_position LIKE ? AND " +
                    "plant_location LIKE ? AND " +
                    "shift LIKE ? " +
                    "GROUP BY work_center";
            PreparedStatement pre_stmt = conn.prepareStatement(sql);
            pre_stmt.setString(1, tm_user_id);
            pre_stmt.setString(2, tm_user_id);
            pre_stmt.setString(3, "Direct");
            pre_stmt.setString(4, "Worker");
            pre_stmt.setString(5, "CZ");
            pre_stmt.setString(6, cn_shift_name);

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
    @Path("/getShiftData/{user_id}")
    public String getShiftData(@PathParam("user_id") String user_id){

        JSONArray result = new JSONArray();
        JSONObject obj_cn_shift_name = new JSONObject();
        JSONObject obj_en_shift_name = new JSONObject();
        JSONObject obj_shift_start = new JSONObject();
        JSONObject obj_shift_finish = new JSONObject();
        JSONObject obj_status = new JSONObject();
        JSONObject obj_msg = new JSONObject();

        String status = "false";
        String msg = "Not found";
        String cn_shift_name = "";
        String en_shift_name = "";
        String shift_start = "";
        int working_hours = 0;

        Connection conn = HrConnect.getConnection();

        try{
            String sql = "SELECT shift AS cn_shift_name " +
                    "FROM V_HRTOCB " +
                    "WHERE employee_id LIKE ?";
            PreparedStatement pre_stmt = conn.prepareStatement(sql);
            pre_stmt.setString(1, user_id);
            ResultSet rs = pre_stmt.executeQuery();
            while (rs.next()){
                cn_shift_name = rs.getString("cn_shift_name");
                status = "true";
                msg = "Data fetched successfully";
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }finally {
            HrConnect.closeConnection();
        }

        if (cn_shift_name.equals("早班")){
            en_shift_name = SHIFT_TYPE_CZ_DAY_SHIFT;
            shift_start = "08:00:00";
            working_hours = 12;
        }else if (cn_shift_name.equals("制造中夜班")){
            en_shift_name = SHIFT_TYPE_CZ_MFG_NIGHT_SHIFT;
            shift_start = "20:00:00";
            working_hours = 12;
        }else if (cn_shift_name.equals("中班")){
            en_shift_name = SHIFT_TYPE_CZ_MIDDLE_SHIFT;
            shift_start = "16:00:00";
            working_hours = 8;
        }else if (cn_shift_name.equals("装配常日班")){
            en_shift_name = SHIFT_TYPE_CZ_ASSY_DAY_SHIFT;
            shift_start = "08:00:00";
            working_hours = 12;
        }else if (cn_shift_name.equals("装配中夜班")){
            en_shift_name = SHIFT_TYPE_CZ_ASSY_NIGHT_SHIFT;
            shift_start = "20:00:00";
            working_hours = 12;
        }else {
            en_shift_name = "ERROR_NO_SHIFT";
            shift_start = "00:00:00";
            working_hours = 0;
        }

        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        String date_shift_start = LocalDate.now().toString() + " " +  shift_start;


        obj_cn_shift_name.put("cn_shift_name", cn_shift_name);
        obj_en_shift_name.put("en_shift_code", en_shift_name);
        try {
            obj_shift_start.put("shift_start", fmt.format(fmt.parse(date_shift_start)));

            Calendar c_shift_finish = Calendar.getInstance();
            c_shift_finish.setTime(fmt.parse(date_shift_start));
            c_shift_finish.add(Calendar.HOUR, working_hours);

            obj_shift_finish.put("shift_finish", fmt.format(c_shift_finish.getTime()));
        }catch (ParseException ex){
            ex.printStackTrace();
        }

        obj_status.put("status", status);
        obj_msg.put("msg", msg);

        result.add(0,obj_status);
        result.add(1,obj_msg);
        result.add(2,obj_cn_shift_name);
        result.add(3,obj_en_shift_name);
        result.add(4,obj_shift_start);
        result.add(5,obj_shift_finish);

        return result.toJSONString();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getEmployeeNameFromHRDB/{employee_id}")
    public String getEmployeeNameFromHRDB(@PathParam("employee_id") String employee_id){
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        String status = "false";
        String msg = "Not found";

        Connection conn = HrConnect.getConnection();
        try{
            String sql = "SELECT " +
                        "employee_name " +
                        "FROM V_HRTOCB " +
                        "WHERE " +
                        "employee_id LIKE ?";
            PreparedStatement pre_stmt = conn.prepareStatement(sql);
            pre_stmt.setString(1,employee_id);
            ResultSet rs = pre_stmt.executeQuery();

            data = Utility.convertResultSetToJson(rs);

            if (data.size() > 0){
                status = "true";
                msg = "Data fetched successfully";
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            return "failed";
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
    @Path("/getCnShiftName/{employee_id}")
    public String getCnShiftName(@PathParam("employee_id") String employee_id){
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        String status = "false";
        String msg = "Not found";

        Connection conn = HrConnect.getConnection();

        try{
            String sql = "SELECT shift AS cn_shift_name " +
                    "FROM V_HRTOCB " +
                    "WHERE employee_id LIKE ?" ;
            PreparedStatement pre_stmt = conn.prepareStatement(sql);
            pre_stmt.setString(1,employee_id);
            ResultSet rs = pre_stmt.executeQuery();

            data = Utility.convertResultSetToJson(rs);

            if (data.size() > 0){
                status = "true";
                msg = "Data fetched successfully";
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            return "failed";
        }finally {
            HrConnect.closeConnection();
        }

        result.put("status", status);
        result.put("msg", msg);
        result.put("data", data);

        return result.toJSONString();
    }

}
