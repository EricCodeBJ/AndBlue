package com.kidevstudio.andblue;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kidevstudio.andblue.Interface.BleutoothStateListener;
import com.kidevstudio.andblue.Interface.onDevicePickerListener;
import com.kidevstudio.andblue.Interface.onReceiveMessage;

public class MainActivity extends AppCompatActivity {

    AndBlue andBlue;

    TextView textView_connectTo, textView_readEditext;
    EditText editext_sendEditext;
    AppCompatButton btn_pickDevice, btn_sendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        andBlue = new AndBlue(MainActivity.this);


        textView_connectTo = findViewById(R.id.connectTo);
        textView_readEditext = findViewById(R.id.readEditext);
        editext_sendEditext = findViewById(R.id.sendEditext);
        btn_pickDevice = findViewById(R.id.pickDevice);
        btn_sendMessage = findViewById(R.id.sendMessage);

        andBlue.setOnReceiveMessage(new onReceiveMessage() {
            @Override
            public void receivedMessage(String message) {
                textView_readEditext.setText(message);
            }
        });

        btn_sendMessage.setOnClickListener(v -> {
            andBlue.sendMessage(editext_sendEditext.getText().toString());
        });


        andBlue.setOnDevicePickerListener(new onDevicePickerListener() {
            @Override
            public void onDevicePick(BluetoothDevice device) {
                andBlue.connectDevice(device);
                textView_connectTo.setText(device.getName());
            }
        });

        btn_pickDevice.setOnClickListener(v -> {
            if ( andBlue.isBTEneable) {
                andBlue.showDevicePicker();
            } else {
                andBlue.startBlueTooth();
            }
        });

        andBlue.setStateListener(new BleutoothStateListener() {

            @Override
            public void onConnecting() {
                super.onConnecting();
                textView_connectTo.setTextColor(Color.parseColor("#1F000000"));
            }

            @Override
            public void onConnected() {
                super.onConnected();
                textView_connectTo.setTextColor(Color.parseColor("#00FF00"));
            }

            @Override
            public void onFailed() {
                super.onFailed();
                textView_connectTo.setTextColor(Color.parseColor("#FF0000"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == AndBlue.REQUEST_ENABLE_BT ) {
            andBlue.setisBTEneable(true);
            andBlue.showDevicePicker();
        }
    }
}