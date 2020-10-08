package com.physi.pac.setter.mqtt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTPublisher {

    private static final String TAG = MQTTPublisher.class.getSimpleName();

//    private static final String BROKER_IP = "192.168.1.12";
    private static final String BROKER_IP = "54.180.153.12";
    private static final String BROKER_PORT = "1883";

    private static final int CONNECTED = 300;
    private static final int DISCONNECTED = 302;

    @SuppressLint("StaticFieldLeak")
    private static MQTTPublisher mPublisher = null;
    private MqttClient mClient = null;
    private MQTTConnectThread mConnectThread = null;

    private Context context;
    private boolean notifyMsg = false;
    private String pubTopic, pubMessage;

    public interface OnConnectedErrorListener{
        void onError();
    }
    private OnConnectedErrorListener connectedErrorListener;

    public void notifyMessage(String topic, String message, OnConnectedErrorListener listener){
        this.pubTopic = topic;
        this.pubMessage = message;
        this.connectedErrorListener = listener;

        if(mClient != null && mClient.isConnected())
            publish(pubTopic, pubMessage);
        else {
            connect();
            notifyMsg = true;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == CONNECTED){
                boolean isConnected = (boolean)msg.obj;
                Log.e(TAG, "Mqtt connected : " + isConnected);
                if(isConnected && notifyMsg){
                    publish(pubTopic, pubMessage);
                    notifyMsg = false;
                }
            }
        }
    };

    private MQTTPublisher(Context context){
        this.context = context;
    }

    public synchronized static MQTTPublisher getInstance(Context context){
        if(mPublisher == null)
            mPublisher = new MQTTPublisher(context);
        return mPublisher;
    }


    public void disconnect(){
        if(mClient != null && mClient.isConnected()){
            try {
                mClient.disconnect();
                handler.obtainMessage(DISCONNECTED).sendToTarget();
                mClient = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        mConnectThread = null;
    }

    public void connect(){
        if(mClient != null && mClient.isConnected())
            return;
//         Get Android Device ID
        @SuppressLint("HardwareIds")
        String android_ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            mClient = new MqttClient("tcp://" + BROKER_IP + ":" + BROKER_PORT, android_ID, new MemoryPersistence());
            mClient.setCallback(mqttCallback);
            mConnectThread = new MQTTConnectThread();
            mConnectThread.start();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publish(String topic, String message){
        try {
            if(mClient != null && mClient.isConnected())
                mClient.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Log.e(TAG, "# MQTT Connection Lost.");
            handler.obtainMessage(DISCONNECTED).sendToTarget();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
//            Log.e(TAG, "messageArrived");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.e(TAG, "deliveryComplete");

        }
    };

    private class MQTTConnectThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                if(mClient != null && mClient.isConnected())
                    return;
                MqttConnectOptions options = new MqttConnectOptions();
                options.setConnectionTimeout(2);
                mClient.connect(options);
            } catch (MqttException e) {
                e.printStackTrace();
                if(connectedErrorListener != null)
                    connectedErrorListener.onError();
            }finally {
                assert mClient != null;
                if(handler != null)
                    handler.obtainMessage(CONNECTED, mClient.isConnected()).sendToTarget();
            }
        }
    }

}
