package com.example.gcs.faster5.util;

import android.content.Context;
import android.net.ConnectivityManager;

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
}
