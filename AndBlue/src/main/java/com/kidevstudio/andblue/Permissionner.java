package com.kidevstudio.andblue;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissionner {

    protected final String TAG = "AndBlue Log";
    private Activity activity;
    private BluetoothAdapter mBluetoothAdapter = null;
    public static final int REQUEST_ENABLE_BT = 2;
    protected boolean isDeviceHaveBT = false, isBTEneable = false;

     // Getter
    public Activity getActivity() {
        return activity;
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }


    // Setter
    protected void setmBluetoothAdapter() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    protected void setisBTEneable(boolean statut) {
        this.isBTEneable = statut;
    }

    protected void setActivity(Activity activity) {
        this.activity = activity;
    }

    protected void requestPermission () {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
        }
    }

    // Others methods
    public boolean checkBTAdapter() {
        if (this.mBluetoothAdapter == null) {
            this.isDeviceHaveBT = false;
        } else {
            this.isDeviceHaveBT = true;
        }
        return isDeviceHaveBT;
    }

     public boolean checkBTStatut() {
         if (this.mBluetoothAdapter != null ) {
             this.isBTEneable = this.mBluetoothAdapter.isEnabled();
         }
         return isBTEneable;
     }

    protected void startBlueTooth() {
        if (isDeviceHaveBT) {
            if (this.mBluetoothAdapter != null && !this.mBluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                getActivity().startActivityForResult(enableBT, REQUEST_ENABLE_BT);
            }
        } else {
            Log.e(TAG, Constantes.MESSAGE_NOT_AVAILABLE);
        }
    }

    public void stopBlueTooth () {
        if (isDeviceHaveBT) {
            if (this.mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        } else {
            Log.e(TAG, Constantes.MESSAGE_NOT_AVAILABLE);
        }
    }

    protected boolean check () {
        boolean reponse = false;
        if (isDeviceHaveBT) {
            if (isBTEneable) {
                return true;
            } else {
                Log.e(TAG, Constantes.MESSAGE_NOT_ENEABLE);
                startBlueTooth();
            }
        } else {
            Log.e(TAG, Constantes.MESSAGE_NOT_AVAILABLE);
        }
        return reponse;
    }
}
