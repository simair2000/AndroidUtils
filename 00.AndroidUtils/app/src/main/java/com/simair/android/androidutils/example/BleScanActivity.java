package com.simair.android.androidutils.example;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.simair.android.androidutils.R;
import com.simair.android.androidutils.ble.BleManager;
import com.simair.android.androidutils.ble.beacon.IBeaconInfo;

import java.util.ArrayList;

public class BleScanActivity extends AppCompatActivity implements BleManager.BleManagerReadyListener, View.OnClickListener, BleManager.ScanResultListener {

    private static final int REQ_ENABLE_BT = 100;
    private static final String TAG = BleScanActivity.class.getSimpleName();
    private BleManager bleManager;
    private boolean isbleManagerReady;
    private ListView listView;
    private ListAdapter listAdapter;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, BleScanActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);
        initView();
        BleManager.getInstance(this).ready(this);
    }

    private void initView() {
        listView = (ListView)findViewById(R.id.listView);
        listAdapter = new ListAdapter(this);
        listView.setAdapter(listAdapter);

        findViewById(R.id.btnScanStart).setOnClickListener(this);
        findViewById(R.id.btnScanStop).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_ENABLE_BT) {
            Log.d(TAG, "onActivityResult : [" + resultCode + "] " + data);
            if(resultCode == RESULT_OK) {
                // enabled
                BleManager.getInstance(this).ready(this);
            } else {
                // not enabled
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bleManager != null) {
            bleManager.scanDevice(false);
        }
    }

    @Override
    public void onBleManagerReady(BleManager manager) {
        bleManager = manager;
        isbleManagerReady = true;
        bleManager.addOnScanResultListener(this);
    }

    @Override
    public void onBleManagerError(BleManager.BleError error) {
        if(error == BleManager.BleError.BLE_NOT_ENABLED) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, REQ_ENABLE_BT);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnScanStart:
                if(isbleManagerReady) {
                    bleManager.scanDevice(true);
                }
                break;
            case R.id.btnScanStop:
                if(isbleManagerReady) {
                    bleManager.scanDevice(false);
                }
                break;
        }
    }

    @Override
    public void onScanResult(ArrayList<ScanResult> list) {
        if(listAdapter != null) {
            ArrayList<IBeaconInfo> tList = new ArrayList<>();
            if(list != null && list.size() > 0) {
                for(ScanResult result : list) {
                    IBeaconInfo beaconInfo = IBeaconInfo.parse(result);
                    if(beaconInfo != null) {
                        tList.add(beaconInfo);
                    }
                }
            }
            listAdapter.setList(tList);
        }
    }

    class ListAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private ArrayList<IBeaconInfo> list;

        public ListAdapter(Context context) {
            inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public IBeaconInfo getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = inflater.inflate(R.layout.ble_scanlist_item, null);
            }
            IBeaconInfo item = getItem(i);
            ((TextView)view.findViewById(R.id.textDeviceName)).setText(item.getName());
            ((TextView)view.findViewById(R.id.textDeviceAddress)).setText(item.getAddress());
            ((TextView)view.findViewById(R.id.textRSSI)).setText("RSSI : " + item.getRssi());
            ((TextView)view.findViewById(R.id.textUUID)).setText("UUID : " + item.getUuid());
            ((TextView)view.findViewById(R.id.textMajor)).setText("Major : " + item.getMajor());
            ((TextView)view.findViewById(R.id.textMinor)).setText("Minor : " + item.getMinor());

            return view;
        }

        public void setList(ArrayList<IBeaconInfo> list) {
            this.list = list;
            notifyDataSetChanged();
        }
    }
}
