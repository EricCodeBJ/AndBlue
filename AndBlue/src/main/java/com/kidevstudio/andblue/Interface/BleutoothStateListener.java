package com.kidevstudio.andblue.Interface;

abstract public class BleutoothStateListener {

    public void onConnected() {};

    public void onConnecting() {};

    public void onLost() {};

    public void onFailed() {};

    public void onNone() {};

    public void onDisonnected() {};

    public void onStateChanged() {};
}
