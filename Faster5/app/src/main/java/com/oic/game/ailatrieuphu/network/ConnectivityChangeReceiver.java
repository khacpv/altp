package com.oic.game.ailatrieuphu.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Kien on 07/28/2016.
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       /* if (intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
            Intent i = new Intent(context.getApplicationContext(), PopupConnection.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            Log.e("Connection changed", "" + intent.getAction());
        }*/
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Log.e("Connection connected", "" + intent.getAction());

            } else {
                Log.e("Connection disconnect", ""+ intent.getAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}