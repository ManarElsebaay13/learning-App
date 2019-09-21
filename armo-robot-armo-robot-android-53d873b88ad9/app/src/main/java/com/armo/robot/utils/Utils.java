package com.armo.robot.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;


public class Utils {


    public static String getIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();

            String ipString = String.format(
                    "%d.%d.%d.%d",
                    (ip & 0xff),
                    (ip >> 8 & 0xff),
                    (ip >> 16 & 0xff),
                    (ip >> 24 & 0xff));

            return ipString;
        } catch (Exception e) {
            return null;
        }

    }


    public static String getStreamUrl(String ipAddress) {
        return String.format("http://%s:%d", ipAddress, Constants.STREAM_PORT);
    }
}
