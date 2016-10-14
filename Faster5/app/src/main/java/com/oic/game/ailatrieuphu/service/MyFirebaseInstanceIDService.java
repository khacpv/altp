package com.oic.game.ailatrieuphu.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.oic.game.ailatrieuphu.util.PrefUtils;

/**
 * Created by kienht on 10/13/16.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private String TAG = MyFirebaseInstanceIDService.class.getName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String fcmToken){
        PrefUtils.getInstance(this).set(PrefUtils.KEY_FCM,fcmToken);

    }
}
