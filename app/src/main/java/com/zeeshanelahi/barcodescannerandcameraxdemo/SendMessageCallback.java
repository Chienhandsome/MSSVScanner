package com.zeeshanelahi.barcodescannerandcameraxdemo;

public interface SendMessageCallback {
    void onMessageSentSucced();
    void onMessageFailed(String  mssv);
}
