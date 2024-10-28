package com.zeeshanelahi.barcodescannerandcameraxdemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    public static Date parseStringToDate(String dateString, String datePattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        return formatter.parse(dateString);
    }
    public static String parseDateToString(Date time, String pattern){
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(time);
    }
}
