package com.simair.android.androidutils.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class BleService extends Service {

    private static final String TAG = BleService.class.getSimpleName();
    private static final long SCAN_PERIOD = 1000;
    private static final long SCAN_INTERVAL = 2000;
    IBinder localBinder = new BleServiceBinder();
    public boolean isSupportBLE;
    private BluetoothAdapter bluetoothAdapter;
    public boolean isBluetoothEnabled;
    private InternalHandler handler = new InternalHandler(this);
    private HashMap<String, ScanResult> scanResultHashMap = new HashMap<>();
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            scanResultHashMap.put(result.getDevice().getAddress(), result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i(TAG, "++ onBatchScanResults ++");
            Log.d(TAG, "results : " + results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(TAG, "++ onScanFailed ++");
            Log.d(TAG, "errorCode : " + errorCode);
        }
    };
    private BleManager bleManager;

    public void scanDevice(boolean scan) {
        Log.d(TAG, "++ scanDevice() ++");
        if(isValid()) {
            if(scan) {
                handler.obtainMessage(WHAT_START_SCAN).sendToTarget();
            } else {
                handler.obtainMessage(WHAT_STOP_SCAN).sendToTarget();
            }
        }
    }

    private boolean isValid() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public void setBleManager(BleManager bleManager) {
        this.bleManager = bleManager;
    }

    class BleServiceBinder extends Binder {
        BleService getService() {
            return BleService.this;
        }
    }

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, BleService.class);
        return i;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "++ onBind ++");
        return localBinder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        Log.i(TAG, "++ onCreate ++");
        super.onCreate();
        // check BLE support
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            isSupportBLE = false;
        } else {
            isSupportBLE = true;
            bluetoothAdapter = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                isBluetoothEnabled = false;
            } else {
                isBluetoothEnabled = true;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "++ onStartCommand ++");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "++ onDestroy ++");
        super.onDestroy();
    }

    private static final int WHAT_START_SCAN = 0;
    private static final int WHAT_SCAN_FINISHED = 1;
    private static final int WHAT_STOP_SCAN = 2;
    private static boolean isScanning;
    private static class InternalHandler extends Handler {
        WeakReference<BleService> reference;
        private BluetoothLeScanner bleScanner;

        InternalHandler(BleService object) {
            reference = new WeakReference<>(object);
        }

        @Override
        public void handleMessage(Message msg) {
            BleService service = reference.get();
            if(service != null) {
                switch (msg.what) {
                    case WHAT_START_SCAN:
                        if(!isScanning) {
                            isScanning = true;
                            bleScanner = service.bluetoothAdapter.getBluetoothLeScanner();
                            bleScanner.startScan(service.scanCallback);
                            sendEmptyMessageDelayed(WHAT_SCAN_FINISHED, SCAN_PERIOD);
                        }
                        break;
                    case WHAT_SCAN_FINISHED:
                        if(service.bleManager != null) {
                            service.bleManager.onScanFinished(service.scanResultHashMap);
                        }
                        isScanning = false;
                        bleScanner.stopScan(service.scanCallback);
                        sendEmptyMessageDelayed(WHAT_START_SCAN, SCAN_INTERVAL);
                        break;
                    case WHAT_STOP_SCAN:
                        Log.e(TAG, "WHAT_STOP_SCAN");
                        bleScanner.stopScan(service.scanCallback);
                        isScanning = false;
                        removeMessages(WHAT_SCAN_FINISHED);
                        removeMessages(WHAT_START_SCAN);
                        break;
                }
            }
        }
    }
}
