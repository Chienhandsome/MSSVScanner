package com.zeeshanelahi.barcodescannerandcameraxdemo.utils;

public class DataChecker {
    public static boolean isMSSV(String mssv) {
        // Regular expression to match MSSV format
        String regex = "^[12][0-9]{6}1$";
        return mssv.matches(regex);
    }
}
