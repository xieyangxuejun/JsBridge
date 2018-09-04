package com.github.lzyzsd.jsbridge.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;

/**
 * Created by silen on 02/05/2018.
 */
public class MainIOSActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ios);
        final BridgeWebView webView = findViewById(R.id.webView);
        webView.setDefaultHandler(new DefaultHandler());

        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl("file:///android_asset/demo_ios.html");

        webView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i("ios", "handler = submitFromWeb, data from web = $data");
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
            }
        });

        MainActivity.User user = new MainActivity.User();
        MainActivity.Location location = new MainActivity.Location();
        location.address = "SDU";
        user.location = location;
        user.name = "大头鬼";

        webView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Log.i("ios", data);
            }
        });

        webView.send("hello");


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.callHandler("functionInJs", "data from Java", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                        Log.i("ios", "reponse data from js $data");
                    }
                });
            }
        });
    }
}