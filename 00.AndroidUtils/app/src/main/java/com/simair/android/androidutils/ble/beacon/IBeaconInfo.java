package com.simair.android.androidutils.ble.beacon;

import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.text.TextUtils;
import android.util.Log;

import com.simair.android.androidutils.Utils;

public class IBeaconInfo {
    private static final String TAG = IBeaconInfo.class.getSimpleName();
    private String name;
    private String address;
    private int rssi;
    private int major;
    private int minor;
    private String uuid;
    private int txPowerLevel;


    public static IBeaconInfo parse(ScanResult result) {
        IBeaconInfo info = null;
        byte[] scanRecord = result.getScanRecord().getBytes();
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            info = new IBeaconInfo();
            info.setName(result.getDevice().getName());
            info.setAddress(result.getDevice().getAddress());
            info.setRssi(result.getRssi());
            info.setTxPowerLevel(result.getScanRecord().getTxPowerLevel());
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
            String hexString = Utils.bytesToHex(uuidBytes);

            //Here is your UUID
            String uuid =  hexString.substring(0,8) + "-" +
                    hexString.substring(8,12) + "-" +
                    hexString.substring(12,16) + "-" +
                    hexString.substring(16,20) + "-" +
                    hexString.substring(20,32);
            Log.e(TAG, "UUID : " + uuid);
            info.setUuid(uuid);

            //Here is your Major value
            int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);
            Log.e(TAG, "major : " + major);
            info.setMajor(major);

            //Here is your Minor value
            int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);
            Log.e(TAG, "minor : " + minor);
            info.setMinor(minor);
        }
        return info;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getTxPowerLevel() {
        return txPowerLevel;
    }

    public void setTxPowerLevel(int txPowerLevel) {
        this.txPowerLevel = txPowerLevel;
    }
}
