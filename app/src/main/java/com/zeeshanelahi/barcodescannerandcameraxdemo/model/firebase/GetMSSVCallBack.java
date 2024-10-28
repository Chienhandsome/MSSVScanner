package com.zeeshanelahi.barcodescannerandcameraxdemo.model.firebase;

import com.zeeshanelahi.barcodescannerandcameraxdemo.model.enities.MSSVInfo;

import java.util.ArrayList;

public interface GetMSSVCallBack {
    void onGetMSSVSuccess(ArrayList<MSSVInfo> mssvInfoList);
}
