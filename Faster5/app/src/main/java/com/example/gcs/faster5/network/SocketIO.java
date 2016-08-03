package com.example.gcs.faster5.network;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;


/**
 * Created by Kien on 08/03/2016.
 */
public class SocketIO extends Application {

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://game.oicmap.com/faster5");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

}
