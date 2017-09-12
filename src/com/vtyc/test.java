package com.vtyc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tmc10427 on 2017/8/10.
 */
public class test {
    public static void main(String[] args) throws ParseException{
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        String thisDate = LocalDate.now().toString() +  " 08:00:00";
        System.out.println(thisDate);
        System.out.println(fmt.format(fmt.parse(thisDate)));
        System.out.println(LocalDate.now());

        Date today = fmt.parse(thisDate);
        System.out.println(fmt.format(today));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.HOUR, 2);

        System.out.println(fmt.format(calendar.getTime()));
        LocalTime shift_end = LocalTime.parse( "08:00:00" );
        shift_end.plusHours(2);
    }
}
