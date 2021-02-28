package com.example.mythingy52app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class Activity2 extends AppCompatActivity {
    TextView name;
    Button getService1;
    Button Disconnect;
    SeekBar dutySeekbar;
    TextView textProgress;
    String TAG = "SecondActivity";
    BluetoothGatt gattConnection;
    List<BluetoothGattService> servicios;
    int dutyValueSeekBar=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        final Intent intent = getIntent();
        final BluetoothDevice device = intent.getParcelableExtra("Example item");
        name = findViewById(R.id.deviceName);
        getService1 = findViewById(R.id.getService1);
        Disconnect = findViewById(R.id.disconnect);
        dutySeekbar = findViewById(R.id.seekBar);
        textProgress = findViewById(R.id.seekBarVal);
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
                BluetoothGattCharacteristic BTCharacWrite = servicios.get(2).getCharacteristic(UUID.fromString("00001527-1212-efde-1523-785feabcd123"));
                if (firstTime == 0)
                    gattConnection.setCharacteristicNotification(BTCharacWrite, true);
                BTCharacWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                BTCharacWrite.setValue(0, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                writefunc = gattConnection.writeCharacteristic(BTCharacWrite);
                gattConnection.close();
                gattConnection = null;
                Intent intent = new Intent(Activity2.this, MainActivity.class);
                startActivity(intent);
            }
        }));
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
            startWriteDuty();
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

    boolean writefunc;
    int firstTime = 0;

    void startWriteDuty(){
        dutySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarVal=0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekBarVal=progress;
                textProgress.setText("Duty Cycle: "+progress+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                dutyValueSeekBar=seekBarVal;
                BluetoothGattCharacteristic BTCharacWrite = servicios.get(2).getCharacteristic(UUID.fromString("00001527-1212-efde-1523-785feabcd123"));
                Log.d(TAG, "Valor del SeekBar? " + dutyValueSeekBar);
                if (firstTime == 0)
                    gattConnection.setCharacteristicNotification(BTCharacWrite, true);
                BTCharacWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

                BTCharacWrite.setValue(seekBarVal, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                writefunc = gattConnection.writeCharacteristic(BTCharacWrite);
                Log.d(TAG, "Esta escribiendo? " + writefunc);
            }
        });
    }


}