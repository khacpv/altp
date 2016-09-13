package com.example.gcs.faster5.sock;

import android.util.Log;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by Kien on 08/03/2016.
 */
public class SockAltp {

    public static final String SERVER_LOCAL = "http://192.168.1.103:8081";

    public static final String SERVER_PROD = "http://altp-oic.rhcloud.com";

    private Socket mSocket;

    private Boolean isConnected = false;

    private ArrayList<OnSocketEvent> listeners = new ArrayList<>();

    private HashMap<String, Emitter.Listener> eventListeners = new HashMap<>();

    /**
     * do not auto connect to SERVER_PROD
     * */
    public SockAltp() {
        this(SERVER_PROD, false);
    }

    /**
     * do not auto connect to url
     * */
    public SockAltp(String url){
        this(url, false);
    }

    /**
     * @param url server uri
     * @param autoConnect auto connect or not
     * */
    public SockAltp(String url, boolean autoConnect) {
        try {
            mSocket = IO.socket(url);

            if (autoConnect) {
                connect();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * start connect to server
     * */
    public void connect() {
        // default listener
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);

        mSocket.connect();
    }

    /**
     * disconnect
     * */
    public void disconnect() {
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);

        for (Map.Entry<String, Emitter.Listener> item : eventListeners.entrySet()) {
            mSocket.off(item.getKey(), item.getValue());
        }

        eventListeners.clear();

        isConnected = false;
    }

    /**
     * send an event to server
     * @param eventName name of event
     * @param data data to send
     * @return Emits an event
     * */
    public Emitter send(String eventName, Object data){
        return mSocket.emit(eventName, data);
    }

    /**
     * @param eventName    name of event
     * @param callback callback should be get on event fired
     */
    public void addEvent(final String eventName, final OnSocketEvent callback) {
        mSocket.off(eventName);
        eventListeners.remove(eventName);
        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                callback.onEvent(eventName, args);

                for (OnSocketEvent event : listeners) {
                    event.onEvent(eventName, args);
                }
            }
        };
        eventListeners.put(eventName, listener);
        mSocket.on(eventName, listener);
    }

    /**
     * register all of events
     * */
    public void addGlobalEvent(OnSocketEvent callback){
        listeners.add(callback);
    }

    /**
     * get status of socket
     */
    public boolean isConnected() {
        return isConnected;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = true;

            for (OnSocketEvent event : listeners) {
                event.onEvent(Socket.EVENT_CONNECT, args);
            }
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            isConnected = false;

            for (OnSocketEvent event : listeners) {
                event.onEvent(Socket.EVENT_DISCONNECT, args);
            }
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            for (OnSocketEvent event : listeners) {
                event.onEvent(Socket.EVENT_CONNECT_ERROR, args);
            }
        }
    };

    private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            for (OnSocketEvent event : listeners) {
                event.onEvent(Socket.EVENT_CONNECT_TIMEOUT, args);
            }
        }
    };

    /**
     * events of connection
     */
    public interface OnSocketEvent {
        void onEvent(String event, Object... args);
    }
}
