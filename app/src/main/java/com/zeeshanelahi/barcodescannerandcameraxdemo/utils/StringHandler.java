package com.zeeshanelahi.barcodescannerandcameraxdemo.utils;

public class StringHandler {
    private final String TAG = "FormatString";
    private static final String[] charNeedToReplace = {"[", "]", "{", "}", "\"", "message:"};
    public static String formatString(String input) {
        input = input.trim();
        input = input.replace("},{", "\n");
        for (String s : charNeedToReplace) {
            input = input.replace(s, "");
        }
        //loai bo cac ki tu sau dau phay
        input = input.replace(",", " ");

        return input;
    }
}
