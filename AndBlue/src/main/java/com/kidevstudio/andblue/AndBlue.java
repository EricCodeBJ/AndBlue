package com.kidevstudio.andblue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kidevstudio.andblue.Interface.BleutoothStateListener;
import com.kidevstudio.andblue.Interface.onDevicePickerListener;
import com.kidevstudio.andblue.Interface.onReceiveMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AndBlue extends Permissionner {

    private Dialog dialog;
    private AlertDialog.Builder builder;
    private BluetoothDevice mDevice;
    private Set<BluetoothDevice> pairedDevice;
    private ArrayList<BluetoothDevice> arrayListPairedDevice;
    private onDevicePickerListener pickerListener;
    private BleutoothStateListener mStateListener;
    private onReceiveMessage mReceiveMessage;

    private boolean BT_CONNECT = false;
    private static BluetoothEchange BTEchange = null;
    private ListAdapter listeDejaUtilise;
    private ListView listBluetooth;
    private TextView view_not_found;
    private List<HashMap<String, String>> data = new ArrayList<>();
    private HashMap<String, String> affichage;

    // Customisation
    private String backgroundColor = "#FFFFFF";
    private int itemLayout = R.layout.item_bt_device, itemLayoutTitleID = R.id.item_title, itemLayoutDescriptionID = R.id.item_description;

    public AndBlue(Activity activity) {
        setActivity(activity);
        setmBluetoothAdapter();
        checkBTAdapter();
        checkBTStatut();

        if ( getCurrentDevice() != null ) {
            BTEchange.connect(mDevice);
        }
    }

    public void setOnDevicePickerListener(onDevicePickerListener deviceListener) {
        this.pickerListener = deviceListener;
    }

    public void setOnReceiveMessage(onReceiveMessage receiveMessage) {
        this.mReceiveMessage = receiveMessage;
    }

    public boolean isBTConnected() { return this.BT_CONNECT;}

    public void setCurrentDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }

    public BluetoothDevice getCurrentDevice() {
        return this.mDevice;
    }

    public void sendMessage(String message) {
        if (isBTConnected()) {
            BTEchange.write(message.getBytes());
        } else {
            Log.e(TAG, Constantes.MESSAGE_NOT_CONNECTED);
        }
    }

    public void setCustomItemLayout(@NonNull int layout, @NonNull int[] textviewID) {
        try {
            if ( textviewID.length == 2 && layout != 0) {
                if ( getActivity().getResources().getResourceName(layout) != null ) {
                    this.itemLayout = layout;
                }
                if ( getActivity().getResources().getResourceName(textviewID[0]) != null ) {
                    this.itemLayoutTitleID = textviewID[0];
                }
                if ( getActivity().getResources().getResourceName(textviewID[1]) != null ) {
                    this.itemLayoutDescriptionID = textviewID[1];
                }
            }
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public void setBackgroundColor(@NonNull String hexColor) {
        this.backgroundColor = hexColor.toLowerCase();
    }

    @SuppressLint("ResourceType")
    public void showDevicePicker() {
        if ( check() ) {
            dialog = null;
            builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AndBlue);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View customLayout = inflater.inflate(R.layout.bt_activity, null);
            builder.setView(customLayout);
            FindViewsById(customLayout);
            dialog = builder.create();
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor(this.backgroundColor)));
            dialog.show();
            PopulateList();
        }
    }

    private void PopulateList() {
        view_not_found.setVisibility(View.GONE);
        listBluetooth.setVisibility(View.GONE);
        BluetoothAdapter mBtadaptater = getmBluetoothAdapter();
        arrayListPairedDevice = new ArrayList<>();

        pairedDevice = mBtadaptater.getBondedDevices();
        int j = 0;
        if(pairedDevice.size() > 0) {
            for(BluetoothDevice device : pairedDevice){
                affichage = new HashMap<String, String>();
                affichage.put("nom", device.getName());
                affichage.put("adress", device.getAddress());
                affichage.put("position", String.valueOf(j));
                arrayListPairedDevice.add(device);
                data.add(affichage);
                j++;
            }
            Collections.sort(data, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> b1, HashMap<String, String> b2) {
                    return b1.get("nom").compareTo(b2.get("nom"));
                }
            });

            listeDejaUtilise = new SimpleAdapter( this.getActivity(), data,
                    itemLayout,
                    new String[]{"nom", "adress"},
                    new int[]{itemLayoutTitleID, itemLayoutDescriptionID}
            );
            listBluetooth.setAdapter(listeDejaUtilise);

            listBluetooth.setVisibility(View.VISIBLE);

            listBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    setCurrentDevice( arrayListPairedDevice.get(Integer.parseInt(data.get(i).get("position"))) );
                    if ( pickerListener != null ) {
                        pickerListener.onDevicePick(arrayListPairedDevice.get(Integer.parseInt(data.get(i).get("position"))));
                    }
                    if ( dialog != null ) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    data.clear();
                    connectDevice(getCurrentDevice());
                }
            });
        } else {
            view_not_found.setVisibility(View.VISIBLE);
        }
    }

    private void FindViewsById(View customLayout) {
        listBluetooth = customLayout.findViewById(R.id.listBluetooth);
        view_not_found = customLayout.findViewById(R.id.view_not_found);
    }

    public void setStateListener(BleutoothStateListener bt){ this.mStateListener = bt;};

    private void stater(Message msg) {
        switch (msg.what) {
            case Constantes.MESSAGE_READ:
                byte[] readuf = (byte[]) msg.obj;
                if (this.mReceiveMessage != null) {
                    this.mReceiveMessage.receivedMessage(new String(readuf, 0, msg.arg1));
                }
            break;
            case Constantes.MESSAGE_DEVICE_NAME:
                if (this.mStateListener != null) { this.mStateListener.onConnected(); }
                BT_CONNECT = true;
            break;
            case Constantes.MESSAGE_STATE_CHANGE :
                if (BluetoothEchange.State != BluetoothEchange.STATE_CONNECTED) {
                    if (BluetoothEchange.State == BluetoothEchange.STATE_CONNECTING) {
                        if (this.mStateListener != null) {this.mStateListener.onConnecting();}
                    } else if (BluetoothEchange.State == BluetoothEchange.STATE_LISTEN) {
                        if (BluetoothEchange.STATE == BluetoothEchange.STATE_LOST){
                            if (this.mStateListener != null) {this.mStateListener.onLost();}
                            BT_CONNECT = false;
                        } else if(BluetoothEchange.STATE == BluetoothEchange.STATE_FAIL){
                            if (this.mStateListener != null) {this.mStateListener.onFailed();}
                            BT_CONNECT = false;
                            mDevice = null;
                        } else if(BluetoothEchange.STATE == BluetoothEchange.STATE_NONE) {
                            BT_CONNECT = false;
                            mDevice = null;
                            if (this.mStateListener != null) {this.mStateListener.onNone();}
                        }
                    }
                } else {
                    BT_CONNECT = true;
                }
            break;
            default:;
        }
    }

    public void connectDevice(BluetoothDevice device) {
        mDevice = getmBluetoothAdapter().getRemoteDevice(device.getAddress());
        BTEchange = new BluetoothEchange(getActivity(),mHandler);
        BTEchange.connect(mDevice);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            stater(msg);
        }
    };

}
