package com.aantik.dexeditorplus;

import android.util.Log;

public class log {

    public static final String TAG = "ANTIK";

    public static void log(Object args) {

        Log.i(TAG, String.valueOf(args));

    }
}