package com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.ApiEndpoint;
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ServerInteractor {
    private static String TAG = "ServerInteractor";
    private String SERVER_URL;
    private static ServerInteractor instance;
    private ServerInteractor() {}
    public static synchronized ServerInteractor getInstance(Context context) {
        if (instance == null) {
            instance = new ServerInteractor();
        }
        String linkServer = ApiFactory.INSTANCE.createApi(ApiEndpoint.MARK_ATTENDANCE, context);
        Log.d(TAG, "getInstance: " + linkServer);
        instance.setSERVER_URL(linkServer);
        return instance;
    }

    public void sendMessageToServer(Context context, String message) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("MSSV", message);

        Log.d(TAG, "link server: " + SERVER_URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SERVER_URL, jsonBody,
                response -> {
                    // Handle response from server
                    Toast.makeText(context, "Gửi thành công !", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    String errorMessage = error.getMessage();
                    if (errorMessage == null) {
                        errorMessage = "Unknown error occurred";
                    }
                    Log.e(TAG, "sendMessageToServer: " + errorMessage);
                    Toast.makeText(context, "Gửi thất bại\nThử kiểm tra link server !", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonObjectRequest);
    }

    public void setSERVER_URL(String SERVER_URL) {
        this.SERVER_URL = SERVER_URL;
    }
}
