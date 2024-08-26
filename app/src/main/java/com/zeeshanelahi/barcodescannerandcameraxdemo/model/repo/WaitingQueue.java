package com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo;

import android.content.Context;
import android.widget.Toast;

import com.zeeshanelahi.barcodescannerandcameraxdemo.SendMessageCallback;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WaitingQueue {
    private Map<String, Boolean> waitingQueue;
    private Map<String, Boolean> submitted;

    private SendMessageCallback callback= new SendMessageCallback() {
        @Override
        public void onMessageSentSucced() {
            submitted.putAll(waitingQueue);
            waitingQueue.clear();
        }

        @Override
        public void onMessageFailed(String mssv) {
            waitingQueue.replaceAll((k, v) -> false);
        }
    };

    private static WaitingQueue instance;
    public static synchronized WaitingQueue getInstance() {
        if (instance == null) {
            instance = new WaitingQueue();
        }
        return instance;
    }

    private WaitingQueue(){
        waitingQueue = new HashMap<>();
        submitted = new HashMap<>();
    }

    public void addToWaitingQueue(String code) {
        waitingQueue.put(code, true);
    }
    public void submit(Context context) throws JSONException {
        if (waitingQueue.isEmpty()){
            return;
        }

        JSONArray jsonArray = new JSONArray(waitingQueue.keySet());
        ServerInteractor.getInstance(context).sendMessageToServer(context, jsonArray, callback);
    }

}
