package com.oic.game.ailatrieuphu;

import android.app.Application;

import com.oic.game.ailatrieuphu.sock.SockAltp;

/**
 * Created by khacpham on 8/5/16.
 */
public class MainApplication extends Application {

    private static SockAltp mSockAltp;

    public static SockAltp sockAltp() {
        if (mSockAltp == null) {
            mSockAltp = new SockAltp(SockAltp.SERVER_PROD, true);
            //mSockAltp = new SockAltp(SockAltp.SERVER_LOCAL, true);
        }
        return mSockAltp;
    }

}
