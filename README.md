| Methods      | Funcions |
| :---        |    ----:   |
| checkBTAdapter      | Title       |
| checkBTStatut   | Text        |
| requestPermission   | Text        |
| startBlueTooth   | Text        |
| showDevicePicker   | Text        |
| setOnDevicePickerListener   | Text        |
| connectDevice   | Text        |
| setStateListener   | Text        |
| getCurrentDevice   | Text        |
| sendMessage   | Text        |
| setOnReceiveMessage   | Text        |
| stopBlueTooth   | Text        |



> andBlue.connectDevice(BluetoothDevice bt);
> andBlue.getmBluetoothAdapter() // BluetoothAdapter btAdapter;
> andBlue.getCurrentDevice() // BluetoothDevice bt;
> andBlue.setStateListener(new BleutoothStateListener());
> andBlue.setOnDevicePickerListener(new onDevicePickerListener());
> andBlue.sendMessage() // void;
> andBlue.isBTConnected() // bool;
> andBlue.startBlueTooth() // void;
> andBlue.stopBlueTooth() // void;
> andBlue.requestPermission() // void;
> andBlue.checkBTAdapter() // bool;
> andBlue.checkBTStatut() // bool;
> andBlue.showDevicePicker();
> andBlue.setOnReceiveMessage(new onReceiveMessage());

AndBlue
===============

A fast Android library to easily manage interactions with a bluetooth module.

![AndBlue](https://raw.github.com/EricCodeBJ/AndBlue/master/app/src/main/res/drawable/Screenshot_20220418-102156.png)
![AndBlue](https://raw.github.com/EricCodeBJ/AndBlue/master/app/src/main/res/drawable/Screenshot_20220417-210959.png)

It uses a BitmapShader and **does not**:
* create a copy of the original bitmap
* use a clipPath (which is neither hardware accelerated nor anti-aliased)
* use setXfermode to clip the bitmap (which means drawing twice to the canvas)

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
    implementation 'com.github.EricCodeBJ:AndBlue:Tag'
}
```

Usage
-----
```java

AndBlue andBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      ...
      andBlue = new AndBlue(MainActivity.this);
      
      andBlue.setOnReceiveMessage(new onReceiveMessage() {
            @Override
            public void receivedMessage(String message) {
            }
        });

        btn_sendMessage.setOnClickListener(v -> {
        });
    }
```



License
-------

    Copyright 2014 - 2020 Henning Dodenhof

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
