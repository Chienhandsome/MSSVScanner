package com.zeeshanelahi.barcodescannerandcameraxdemo.model.enities;

public class MSSVInfo {
    private String mssv;
    private String scanTime;

    public MSSVInfo() {
    }

    public MSSVInfo(String mssv, String scanTime) {
        this.mssv = mssv;
        this.scanTime = scanTime;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }
}
