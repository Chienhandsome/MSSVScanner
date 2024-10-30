package com.zeeshanelahi.barcodescannerandcameraxdemo.utils;

public class MssvKeyHandler {
    public static String handle(String input) {
        // Split the input string by space
        String[] parts = input.split(" ");
        // Return the first part which is the MSSV
        return parts[0];
    }
}
