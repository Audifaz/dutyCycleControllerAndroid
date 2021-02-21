package com.example.mythingy52app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity<addAdapter> extends AppCompatActivity implements scanAdapter.ScanItemClickListener{

    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;

    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 4;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public int i=0;
    public LinkedList<String> list= new LinkedList<String>();
    private BluetoothLeScanner bluetoothLeScanner =
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private boolean mScanning;
    private Handler mHandler;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    public static final String TAG = "BluetoothScan";
    private RecyclerView mRecyclerView;
    private scanAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        checkLocationPermission();
        mRecyclerView= findViewById(R.id.my_recycler_view);
        mAdapter= new scanAdapter(this, list,this );
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ENABLE_BLUETOOTH_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    Toast.makeText(this, "Bluetooth was enabled successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Bluetooth was not enabled successfully", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void checkBluetoothEnable(View view) {
        mAdapter.notifyDataSetChanged();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
        }
    }



    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            Toast.makeText(this, "Location enabled", Toast.LENGTH_SHORT).show();
            return true;
        }
    }



/*
    List<ScanResult> devices=new ArrayList<ScanResult>();
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "Device:"+result.getDevice().getName());
            list.addLast("Device:"+result.getDevice().getName());
            devices.add(result);
            mAdapter.notifyDataSetChanged();
        }
    };
*/

    List <ScanResult> devices = new ArrayList <ScanResult>(1);
    int comp=1;
    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            boolean flag = false;
            super.onScanResult(callbackType, result);
            if(devices.size()>0){
                //Log.d(TAG,"Comparation: "+comp);
                for (int i = 0; i < (devices.size()); i++) {
                    if ((devices.get(i).getDevice().toString()).equals(result.getDevice().toString())) {
                        flag = true;
                    }
                }
                comp++;
                if (flag == false) {
                    list.addLast("Device:" + result.getDevice().getName());
                    devices.add(result);
                    mAdapter.notifyDataSetChanged();
                }
            }
            else{
                list.addLast("Device:" + result.getDevice().getName());
                devices.add(result);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    public void startScan(View view){
        //scanLeDevice(true);
        //bluetoothLeScanner.startScan(mScanCallback);
        List<ScanFilter> filterList =null;
        filterList = new ArrayList<>();
        ScanFilter scanFilter = new ScanFilter.Builder().
                setServiceUuid(ParcelUuid.fromString("ef680100-9b35-4933-9b10-52ffa9740042")).build();
        filterList.add(scanFilter);
        ScanSettings scanSettings = new ScanSettings.Builder().build();
        //bluetoothLeScanner.startScan(filterList, scanSettings, mScanCallback);
        bluetoothLeScanner.startScan(mScanCallback);
    }

    public void stopScan(View view){
        //scanLeDevice(false);
        //bluetoothLeScanner.stopScan(mScanCallback);
        bluetoothLeScanner.stopScan(mScanCallback);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                        Toast.makeText(this, "Location was enabled successfully", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Location was not enabled successfully", Toast.LENGTH_SHORT).show();
                    checkLocationPermission();
                }
                return;
            }

        }
    }

    @Override
    public void onScanItemClick(int position) {
        //Toast.makeText(this, devices.get(position).getDevice().getName(), Toast.LENGTH_SHORT).show();
        bluetoothLeScanner.stopScan(mScanCallback);
        Intent intent = new Intent(MainActivity.this , Activity2.class);
        intent.putExtra("Example item", devices.get(position).getDevice());
        startActivity(intent);
        //BluetoothGatt test=devices.get(position).getDevice().connectGatt(this, false, gattCallback);
    }


    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(status==BluetoothGatt.GATT_SUCCESS){
                if(newState== BluetoothProfile.STATE_CONNECTED){
                    Log.d(TAG, "Bluetooth connected");
                    gatt.discoverServices();
                }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
                    Log.d(TAG,  "Bluetooth disconnected");

                }
            }else{
                Log.d(TAG, "Bluetooth error!!!!!!");

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            List<BluetoothGattService> servicios =gatt.getServices();
            Log.d(TAG, "Número de servicios: "+servicios.size());
            for(int i=0; i<servicios.size();i++){
                Log.d(TAG,"UUID del Servicio: " + servicios.get(i).getUuid().toString() + " Su nombre es: " + servicios.get(i).toString());
            }
        }
    };


    /*private void scanLeDevice(final boolean enable) {
        if (enable) {
            //stops scanning after a pre-defined scan period
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "BLE// mLEScanner.stopScan(mScanCallback) ");
                    bluetoothLeScanner.stopScan(mScanCallback);
                }
            }
        , SCAN_PERIOD);
        Log.d(TAG,"BLE// mLEScanner.startScan(filters, settings, mScanCallback)");
        bluetoothLeScanner.startScan(mScanCallback);
        } else {
            Log.d(TAG,"BLE// mLEScanner.stopScan(mScanCallback)");
            bluetoothLeScanner.stopScan(mScanCallback);
        }
    }
    */

}