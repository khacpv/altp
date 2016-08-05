package com.example.gcs.faster5;

import android.app.Application;

import com.example.gcs.faster5.sock.SockAltp;

/**
 * Created by khacpham on 8/5/16.
 */
public class MainApplication extends Application {

    private static SockAltp mSockAltp;

    public static SockAltp sockAltp(){
        if(mSockAltp == null){
            mSockAltp = new SockAltp(SockAltp.SERVER_PROD,true);
        }
        return mSockAltp;
    }
}
