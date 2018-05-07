package com.github.lzyzsd.jsbridge;

import android.util.Log;

/**
 * Created by silen on 07/05/2018.
 */

public class DefaultCallBackFunction implements CallBackFunction {

    @Override
    public void onCallBack(String data) {
        Log.d("web", data);
    }
}
