package com.example.mythingy52app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class Activity2 extends AppCompatActivity {
    TextView name;
    TextView service1;
    TextView buttonState;
    Button getService1;
    Button Disconnect;
    String TAG = "SecondActivity";
    BluetoothGatt gattConnection;
    int BUTTON_PRESSED = 1;
    int BUTTON_RELEASED = 0;
    List<BluetoothGattService> servicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        final Intent intent = getIntent();
        final BluetoothDevice device = intent.getParcelableExtra("Example item");
        name = findViewById(R.id.deviceName);
        service1 = findViewById(R.id.phyButtonStatus);
        buttonState = findViewById(R.id.buttonState);
        getService1 = findViewById(R.id.getService1);
        Disconnect = findViewById(R.id.disconnect);
        name.setText(device.getName());
        getService1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gattConnection = device.connectGatt(getApplicationContext(), false, gattCallback);
            }
        });
        Disconnect.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gattConnection.close();
                gattConnection = null;
                Intent intent = new Intent(Activity2.this, MainActivity.class);
                startActivity(intent);
            }
        }));
        IntentFilter filter = new IntentFilter("com.example.mythingy52.app.MY_INTENT");
        registerReceiver(gattUpdate, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(gattUpdate);
    }

    //BluetoothGattCharacteristic BTCharacWrite = new BluetoothGattCharacteristic()
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        Intent broadcastUpdate = new Intent();

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                    Log.d(TAG, "Bluetooth connected");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "Bluetooth disconnected");
                    gattConnection.close();
                    gattConnection = null;
                    Intent intent = new Intent(Activity2.this, MainActivity.class);
                    startActivity(intent);
                }
            } else {
                Log.d(TAG, "Bluetooth error!!!!!!");
                gattConnection.close();
                gattConnection = null;
                Intent intent = new Intent(Activity2.this, MainActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            servicios = gattConnection.getServices();
        }


        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            broadcastUpdate.setAction("com.example.mythingy52.app.MY_INTENT");
            if (characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0) == BUTTON_PRESSED) {
                broadcastUpdate.putExtra("value", "Button On");
                Log.d(TAG, "Botón oprimido");
            } else {
                broadcastUpdate.putExtra("value", "Button Off");
                Log.d(TAG, "Botón ya no oprimido");
            }
            sendBroadcast(broadcastUpdate);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Funciono la escritura?");
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    public void connectPhysicalDevice(View view) {
        BluetoothGattCharacteristic BTCharac = servicios.get(2).getCharacteristic(UUID.fromString("00001524-1212-efde-1523-785feabcd123"));
        gattConnection.setCharacteristicNotification(BTCharac, true);
        BluetoothGattDescriptor descriptor = BTCharac.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean success = gattConnection.writeDescriptor(descriptor);
        Log.d(TAG, "The notification was enabled successfully?" + success);
        service1.setText("It is enabled the button subscription");
    }

    private final BroadcastReceiver gattUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String input = intent.getStringExtra("value");
            buttonState.setText(input);
        }
    };

    boolean turnLed = false;
    boolean writefunc;
    int firstTime = 0;

    public void sendData(View view) {
        BluetoothGattCharacteristic BTCharacWrite = servicios.get(2).getCharacteristic(UUID.fromString("00001525-1212-efde-1523-785feabcd123"));
        if (firstTime == 0)
            gattConnection.setCharacteristicNotification(BTCharacWrite, true);
            BTCharacWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        if (!turnLed) {
            BTCharacWrite.setValue(1, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            turnLed = true;
        } else {
            BTCharacWrite.setValue(0, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            turnLed = false;
        }
        writefunc = gattConnection.writeCharacteristic(BTCharacWrite);
        Log.d(TAG, "Esta escribiendo? " + writefunc);
    }
}