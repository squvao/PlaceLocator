package com.example.kirill.placelocator.APIUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
In this activity a method of checking internet access takes place.
If internet connection is absent, then our app doesn't start and pops up a white screen.
This method is invoked in CheckActivity.
 */
public class Network {
    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        return wifiInfo != null && wifiInfo.isConnected();
    }
}
