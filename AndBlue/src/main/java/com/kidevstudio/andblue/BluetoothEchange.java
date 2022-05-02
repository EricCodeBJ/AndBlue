package com.kidevstudio.andblue;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Handler;

public class BluetoothEchange {

    private static final String NAME = "BluetoothEchange";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //00001101-0000-1000-8000-00805F9B34FB

    private final BluetoothAdapter mAdapter;
    private final android.os.Handler mHandler;
    private int mState;

    public static int State;
    public static int STATE;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_LOST = 4;
    public static final int STATE_FAIL = 5;

    private AcceptThreadLampe mAcceptThread;
    private ConnectThreadLampe mConnectThread;
    private ConnectedThreadLampe mConnectedThread;

    public BluetoothEchange(Context context, android.os.Handler handler){
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        mState = STATE_NONE;
        State = STATE_NONE;
    }

    private synchronized void setState(int state){
        mState = state;
        State = state;
        mHandler.obtainMessage(Constantes.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState(){
        return mState;
    }

    public synchronized void start(){
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        //
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        //
        if(mAcceptThread != null){
            mAcceptThread = new AcceptThreadLampe();
            mAcceptThread.start();
        }
        //
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device){
        if(mState == STATE_CONNECTING){
            if(mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        Log.e("MyHome","Etat 1");
        //
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        Log.e("MyHome","Etat 2");
        //
        mConnectThread = new ConnectThreadLampe(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        //
        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        //
        mConnectedThread = new ConnectedThreadLampe(socket);
        mConnectedThread.start();
        //
        Message msg = mHandler.obtainMessage(Constantes.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
        Log.e("MyHome","Mode connecté");
    }

    public synchronized void stop(){
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        //
        if(mAcceptThread != null){
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        //
        setState(STATE_NONE);
        STATE = STATE_NONE;
    }

    public void write(byte[] out){
        ConnectedThreadLampe r;
        synchronized (this){
            if(mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        //
        r.write(out);
    }

    public void connectionFailed(){
        setState(STATE_LISTEN);
        STATE = STATE_FAIL;
        Message msg = mHandler.obtainMessage(Constantes.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.TOAST,"Association échouée/ou déjà connecté");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private void connectionLost(){
        setState(STATE_LISTEN);
        STATE = STATE_LOST;
        Message msg = mHandler.obtainMessage(Constantes.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.TOAST,"Association au périphérique rompue");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private class AcceptThreadLampe extends Thread{
        private final BluetoothServerSocket mmServerSocket;
        //
        public AcceptThreadLampe(){
            BluetoothServerSocket tmp = null;
            try{
                UUID MY_UUIDs = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //00001101-0000-1000-8000-00805F9B34FB
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME,MY_UUIDs);
            }
            catch (IOException e){
            }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            while(mState != STATE_CONNECTED){
                try{
                    socket = mmServerSocket.accept();
                }
                catch (IOException e){
                    break;
                }
                //
                if(socket != null){
                    synchronized (BluetoothEchange.this){
                        switch (mState){
                            case STATE_LISTEN :
                            case STATE_CONNECTING :
                                connected(socket,socket.getRemoteDevice());
                                break;
                            case STATE_NONE :
                            case STATE_CONNECTED :
                                try{
                                    socket.close();
                                }
                                catch (IOException e){

                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel(){
            try{
                mmServerSocket.close();
            }
            catch (IOException e){

            }
        }
    }

    private class ConnectThreadLampe extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThreadLampe(BluetoothDevice device){
            mmDevice = device;
            BluetoothSocket tmp = null;
            try{
                //tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                Log.e("MyHome","Name : "+device.getName()+" Adresse : "+device.getAddress());
            }
            catch (IOException e){
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            //
            setName("ConnectThread");
            //
            mAdapter.cancelDiscovery();
            try{
                mmSocket.connect();
                Log.e("MyHome","Module connecté");
            }
            catch (IOException e){
                connectionFailed();
                Log.e("pourquoi erreur 2", e.getMessage().toString());
                try {
                    mmSocket.close();
                }
                catch (IOException e2){
                    Log.e("pourquoi erreur 3", e2.getMessage().toString());
                }
                BluetoothEchange.this.start();
                return;
            }
            synchronized (BluetoothEchange.this){
                mConnectThread = null;
            }
            connected(mmSocket,mmDevice);
            Log.e("MyHome","Connecxion lancé");
        }

        public void cancel(){
            try{
                mmSocket.close();
            }
            catch (IOException e){
            }
        }
    }

    private class ConnectedThreadLampe extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThreadLampe(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            //
            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e){

            }
            //
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            //
            while(true){
                try{
                    Thread.sleep(200);
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(Constantes.MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                }catch (Exception e){
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer){
            try{
                mmOutStream.write(buffer);
                mHandler.obtainMessage(Constantes.MESSAGE_WRITE,-1,-1,buffer).sendToTarget();
            }catch (IOException e){

            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e){

            }
        }
    }
}
