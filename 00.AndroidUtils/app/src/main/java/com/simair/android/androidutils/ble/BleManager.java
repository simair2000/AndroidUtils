package com.simair.android.androidutils.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class BleManager {
    private static final String TAG = BleManager.class.getSimpleName();
    private static volatile BleManager instance;
    private Context context;
    private ServiceConnection bleServiceConnection;
    private BleService bleService;
    private boolean isServiceConnected;
    private BleManagerReadyListener listener;
    private ArrayList<ScanResultListener> scanResultListeners = new ArrayList<>();

    public interface ScanResultListener {
        public void onScanResult(ArrayList<ScanResult> list);
    }

    public void onScanFinished(HashMap<String, ScanResult> scanResultHashMap) {
        ArrayList<ScanResult> results = new ArrayList<>();
        if(scanResultHashMap != null && scanResultHashMap.size() > 0) {
            Set<String> keys = scanResultHashMap.keySet();
            for(String key : keys) {
                ScanResult result = scanResultHashMap.get(key);
                results.add(result);
                int status = result.getDevice().getBondState();
                String bond = "Not Bonded";
                if(status == BluetoothDevice.BOND_BONDED) {
                    bond = "Bonded";
                }
                Log.w(TAG, result.getDevice().getName() + "[" + result.getDevice().getAddress() + "] " + result.getRssi() + " - " + bond);
            }
        }
        if(scanResultListeners != null && scanResultListeners.size() > 0) {
            for(ScanResultListener l : scanResultListeners) {
                ArrayList<ScanResult> res = new ArrayList<>();
                res.addAll(results);
                l.onScanResult(res);
            }
        }
    }

    public void addOnScanResultListener(ScanResultListener l) {
        if(l != null) {
            scanResultListeners.add(l);
        }
    }

    public void removeScanResultListener(ScanResultListener l) {
        if(l != null) {
            scanResultListeners.remove(l);
        }
    }

    public enum BleError {
        BLE_NOT_SUPPORT,
        BLE_NOT_ENABLED,
    }

    public interface BleManagerReadyListener {
        public void onBleManagerReady(BleManager manager);
        public void onBleManagerError(BleError error);
    }

    public void ready(BleManagerReadyListener l) {
        if(l == null) {
            return;
        }
        this.listener = l;
        if(isServiceConnected) {
            if(!bleService.isSupportBLE) {
                l.onBleManagerError(BleError.BLE_NOT_SUPPORT);
            } else if(!bleService.isBluetoothEnabled) {
                l.onBleManagerError(BleError.BLE_NOT_ENABLED);
            } else {
                l.onBleManagerReady(this);
            }
        } else {
            bindBleService();
        }
    }

    public static BleManager getInstance(Context context) {
        if(instance == null) {
            synchronized (BleManager.class) {
                instance = new BleManager(context);
            }
        }
        return instance;
    }

    private BleManager(final Context context) {
        this.context = context;
        isServiceConnected = false;
        bleServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "++ onServiceConnected ++");
                isServiceConnected = true;
                bleService = ((BleService.BleServiceBinder)iBinder).getService();
                if(listener != null) {
                    listener.onBleManagerReady(BleManager.this);
                }
                bleService.setBleManager(BleManager.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i(TAG, "++ onServiceDisconnected ++");
                isServiceConnected = false;
                bleService = null;
            }
        };
    }

    private void bindBleService() {
        Log.i(TAG, "++ bindBleService ++");
        if(!isServiceConnected) {
            context.bindService(BleService.getIntent(context), bleServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindBleService() {
        Log.i(TAG, "++ unbindBleService ++");
        if(isServiceConnected) {
            context.unbindService(bleServiceConnection);
        }
    }

    public void scanDevice(boolean scan) {
        if(isServiceConnected) {
            bleService.scanDevice(scan);
        }
    }
}
