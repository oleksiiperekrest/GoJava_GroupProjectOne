package com.gmail.fomichov.m.youtubeanalytics.utils;

import android.util.Log;

public class MyLog {
    private static String TAG = "myApp";

    private MyLog() {
    }

    public static void showLog(String textLog) {
        Log.v(TAG, textLog);
    }
}
