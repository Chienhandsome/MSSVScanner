package com.zeeshanelahi.barcodescannerandcameraxdemo.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerInteractor {
    private String TAG = "ServerInteractor";
    private String SERVER_URL;
    private static ServerInteractor instance;
    private ServerInteractor() {}
    public static synchronized ServerInteractor getInstance(Context context) {
        if (instance == null) {
            instance = new ServerInteractor();
        }
        String linkServer = SharedPreferencesHelper.getInstance(context).getString(StringValue.LINK_SERVER_KEY, StringValue.LINK_SERVER_DEFAULT);
        instance.setSERVER_URL(linkServer);
        return instance;
    }

    public void sendMessageToServer(Context context, String message) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("MSSV", message);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SERVER_URL, jsonBody,
                response -> {
                    // Handle response from server
                    Toast.makeText(context, "Gửi thành công !", Toast.LENGTH_SHORT).show();
                }
                ,
                error -> {
                    // Handle error
                    //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "sendMessageToServer: "+ error.getMessage());
                    if(error.getMessage().contains("java.net.UnknownHostException: Unable to resolve host")){
                        Toast.makeText(context, "Kiểm tra lại kết nối internet !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Gửi thất bại\nThử kiểm tra link server !", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonObjectRequest);
    }

    public void setSERVER_URL(String SERVER_URL) {
        this.SERVER_URL = SERVER_URL;
    }
}
