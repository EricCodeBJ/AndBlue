AndBlue
===============

A fast Android library to easily manage interactions with a bluetooth module. such as :
- ✅ check the availability of the Bluetooth device
- ✅ ask permission to use Bluetooth
- ✅ start and turn off the Bluetooth
- ✅ choose the Bluetooth device to interact with
- ✅ send and receive messages
- ✅ listen to Bluetooth device states

<p float="left">
  <img src="https://raw.githubusercontent.com/EricCodeBJ/AndBlue/master/app/src/main/res/drawable/screenshot_20220418_102156.png" width="300px" />
  <img src="https://raw.githubusercontent.com/EricCodeBJ/AndBlue/master/app/src/main/res/drawable/screenshot_20220417_210959.png" width="300px" /> 
</p>


Installation
------

**Step 1.** Add the following permissions in the **Manifest.xml**

```
  <uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
  <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
  <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
  <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```
    
**Step 2.** Add the JitPack repository to your build file

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

**Step 3.** Add the dependency

```
dependencies {
    implementation 'com.github.EricCodeBJ:AndBlue:v1'
}
```

Usage
-----
```java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      ...
      AndBlue andBlue = new AndBlue(MainActivity.this);
      
      // Start Bluetooth
      andBlue.startBlueTooth();
      
      /* Show already paired devices
      Make sure the Bluetooth device is turned on before calling this method
      You can use **onActivityResult** method below
      */
      andBlue.showDevicePicker();
      
      // Send message
      andBlue.sendMessage("message");
        
      // On receive message
      andBlue.setOnReceiveMessage(new onReceiveMessage() {
            @Override
            public void receivedMessage(String message) {
               Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ...
        if (resultCode == Activity.RESULT_OK && requestCode == AndBlue.REQUEST_ENABLE_BT ) {
            andBlue.setisBTEneable(true);
            andBlue.showDevicePicker();
        }
    }
```

All Methods
------

| Name | Return | Description |
| :--- | :----: | :---- |
| checkBTAdapter | boolean | To check if the phone has Bluetooth equipment |
| checkBTStatut | boolean | To check the status (on or off) of the Bluetooth device |
| isBTConnected | boolean | To check if the Bluetooth device has successfully paired with the external device |
| requestPermission | | To request permission to use Bluetooth equipment |
| startBlueTooth | | To start the Bluetooth device |
| stopBlueTooth | | To stop Bluetooth equipment |
| showDevicePicker | | To view the list of already paired external Bluetooth devices |
| setOnDevicePickerListener | | When the user chooses a device from the list |
| setCustomItemLayout | | Allows you to define the display model for items in the list of devices already paired |
| setBackgroundColor | | To set the background color of the page containing the list of devices already paired |
| connectDevice | | To connect to an external device |
| setStateListener | | To know the result of the connection to the external device. This method implements an interface with the following methods: **onConnected(), onConnecting(), onLost(), onFailed(), onNone(), onDisonnected(), onStateChanged()** |
| getCurrentDevice | BluetoothDevice | Gets the connected external device |
| setCurrentDevice | | Sets the current connected external device |
| sendMessage | | To send a message to the connected external device |
| setOnReceiveMessage | | To receive a message from the connected external device |


Advanced
------

**Customisation** 
```
   // Set background  of the bluetooth device picker page
   andBlue.setBackgroundColor("#1cde34");

  // Set custom List item layout
  andBlue.setCustomItemLayout(R.layout.item_bt_device, new int[] {R.id.textview_title, R.id.textview_description});
```

**Device picker listener**
```
andBlue.setOnDevicePickerListener(new onDevicePickerListener() {
    @Override
    public void onDevicePick(BluetoothDevice device) {
         Toast.makeText(MainActivity.this, device.getName(), Toast.LENGTH_SHORT).show();
    }
});
```

**Bluetooth state listener**
```
andBlue.setStateListener(new BleutoothStateListener() {
    @Override
    public void onConnected() {
        Toast.makeText(MainActivity.this, "BT connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisonnected() {
        Toast.makeText(MainActivity.this, "BT disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed() {
        Toast.makeText(MainActivity.this, "BT connexion failed", Toast.LENGTH_SHORT).show();
    }
    
    @Override
      public void onConnecting() {
          Toast.makeText(MainActivity.this, "BT is connecting", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onLost() {
          Toast.makeText(MainActivity.this, "BT connexion lost", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onNone() {
          Toast.makeText(MainActivity.this, "Nothing happen", Toast.LENGTH_SHORT).show();
      }
      
      @Override
      public void onDisonnected() {
          Toast.makeText(MainActivity.this, "BT disonnected", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onStateChanged() {
          Toast.makeText(MainActivity.this, "BT state changed", Toast.LENGTH_SHORT).show();
      }
});
```

**Sample code <a href="https://github.com/EricCodeBJ/AndBlue/blob/master/app/src/main/java/com/kidevstudio/andblue/MainActivity.java" target="blank" title="Sample code">View</a>**

License
-------

Copyright 2022 DEKOUN Cédric

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
