package com.zeeshanelahi.barcodescannerandcameraxdemo.utils;

public class DataChecker {
    public static boolean isMSSV(String mssv){
        if (mssv.length() != 8) {
            return false;
        }
        if (mssv.charAt(0) != '2' && mssv.charAt(0) != '1') {
            return false;
        }
        if (mssv.charAt(1) < '0' || mssv.charAt(1) > '9') {
            return false;
        }
        if (mssv.charAt(7) != '1') {
            return false;
        }
        for (int i = 2; i < 7; i++) {
            if (mssv.charAt(i) < '0' || mssv.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }
}
