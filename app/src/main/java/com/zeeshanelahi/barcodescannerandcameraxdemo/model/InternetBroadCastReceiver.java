package com.zeeshanelahi.barcodescannerandcameraxdemo.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.zeeshanelahi.barcodescannerandcameraxdemo.view.main.InternetStateListenerer;

public class InternetBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "InternetBroadCastReceiver";
    private boolean isConnecting;
    private InternetStateListenerer internetStateListenerer;
    private static InternetBroadCastReceiver instance;

    private InternetBroadCastReceiver() {
        isConnecting = false;
    }

    private InternetBroadCastReceiver(InternetStateListenerer internetStateListenerer) {
        this.internetStateListenerer = internetStateListenerer;
        isConnecting = false;
    }

    public static synchronized InternetBroadCastReceiver getInstance(InternetStateListenerer internetStateListenerer) {
        if (instance == null) {
            instance = new InternetBroadCastReceiver(internetStateListenerer);
        }
        return instance;
    }

    public static synchronized InternetBroadCastReceiver getInstance() {
        if (instance == null) {
            instance = new InternetBroadCastReceiver();
        }
        return instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            if (isNetWorkAvailable(context) && !isConnecting){
                isConnecting = true;

                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(StringValue.RECONNECT_ACTION));
                //Toast.makeText(context, "Available Network", Toast.LENGTH_SHORT).show();
                internetStateListenerer.onConnected();
                Log.d(TAG, "Available Network ");
            }else if (!isNetWorkAvailable(context) && isConnecting){
                isConnecting = false;
                //Toast.makeText(context, "Unavailable Network", Toast.LENGTH_SHORT).show();
                internetStateListenerer.onDisconnected();
                Log.d(TAG, "Unavailable Network ");
            }
        }
    }

    public boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null){
            return false;
        }
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    public void activeBroadCast(Context context){
        InternetBroadCastReceiver internetBroadCastReceiver = instance;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(internetBroadCastReceiver, intentFilter);
    }

    public void deactiveBroadCast(Context context){
        InternetBroadCastReceiver internetBroadCastReceiver = instance;
        context.unregisterReceiver(internetBroadCastReceiver);
    }
}
