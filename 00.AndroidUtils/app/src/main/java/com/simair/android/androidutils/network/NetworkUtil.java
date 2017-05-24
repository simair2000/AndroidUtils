package com.simair.android.androidutils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.network.http.Network;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by simair on 16. 11. 17.
 */

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();

    public static final int NONE = 0;
    public static final int WIFI = 1;

    public static final int MOBILE = 2;
    //
    public static final int RAT_ETC = 0;
    public static final int RAT_3G = 1;
    public static final int RAT_LTE = 2;

    public static int getCellId(Context context) {
        int cellId = 0;
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        CellLocation location = (CellLocation) manager.getCellLocation();
        if (location != null) {
            if (location instanceof GsmCellLocation) {
                cellId = ((GsmCellLocation) location).getCid();
            } else if (location instanceof CdmaCellLocation) {
                cellId = ((CdmaCellLocation) location).getBaseStationId();
            }
        }

        return cellId;
    }

    public static int getRatType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (network != null) {
            if (network.getSubtype() == 13 && network.getSubtypeName().equals("LTE")) {
                return RAT_LTE;
            } else {
                return RAT_3G;
            }
        }

        return RAT_ETC;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isConnectedOrConnecting();
        }

        return false;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().isConnected();
        }

        return false;
    }

    public static String valueOf(int code) {
        switch (code) {
            case WIFI:
                return "WIFI";
            case MOBILE:
                return "MOBILE";
        }

        return "NONE";
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return manager.getActiveNetworkInfo().getTypeName();
        } catch (Exception ignore) {
            ;
        }

        return "";
    }

    public static void setMobileDataEnabled(Context context, boolean enabled) {
        Log.d(TAG, "setMobileDataEnabled(" + enabled + ")");
        try {
            // ConnectivityManager manager =
            // (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // Method setMobileDataEnabled = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled",
            // boolean.class);
            //
            // setMobileDataEnabled.setAccessible(true);
            // setMobileDataEnabled.invoke(manager, enabled);

            final ConnectivityManager conman = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");

            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod(
                    "setMobileDataEnabled", Boolean.TYPE);

            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    /**
     * 네트워크에 연결 되어 있는지 체크한다. wifi나 데이터 연결
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifi.isConnected() || mobile.isConnected());
    }


    /**
     * wifi가 연결되어 있는지 체크
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (wifi.isConnected());

    }
    /**
     * 현재 폰의 usim의 통신사를 조회해서 SKT이면 true 아니면 false리턴한다.
     * @param context
     * @return
     */
    public static boolean isSKTOpterator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getNetworkOperatorName().equalsIgnoreCase("SKTelecom")) {
            return true;
        }
        if (tm.getSimOperatorName().equalsIgnoreCase("SKTelecom")) {
            return true;
        }
        return false;
    }

    public static void getLocalIpAddress(Context context, Command.CommandListener listener) {
        new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException {
                String ip = Network.get("http", "wtfismyip.com", "text", null, null);
                data.putString("ip", ip.trim());
            }
        }.setOnCommandListener(listener).showWaitDialog(context).start();
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
