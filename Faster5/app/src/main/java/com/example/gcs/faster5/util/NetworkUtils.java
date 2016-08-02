package com.example.gcs.faster5.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.example.gcs.faster5.ui.activity.PopupConnection;

/**
 * Created by khacpham on 7/16/16.
 */
public class NetworkUtils {

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return mConnectivityManager.getActiveNetworkInfo() != null
                && mConnectivityManager.getActiveNetworkInfo().isAvailable()
                && mConnectivityManager.getActiveNetworkInfo().isConnected();
    }
    public static void movePopupConnection(Context context){
        Intent intent = new Intent(context.getApplicationContext(), PopupConnection.class);
        context.startActivity(intent);
    }

}
