package com.camp.campusmvp.utils;

import android.util.Log;

/**
 * Created by Administrator on 2021/6/26.
 */

public final class Hlog {
    private Hlog() {}

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }
}
