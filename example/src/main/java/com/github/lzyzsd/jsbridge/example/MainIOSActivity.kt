package com.github.lzyzsd.jsbridge.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebChromeClient
import com.github.lzyzsd.jsbridge.DefaultHandler
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_ios.*

/**
 * Created by silen on 02/05/2018.
 */
class MainIOSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ios)

        webView.setDefaultHandler(DefaultHandler())

        webView.webChromeClient = object : WebChromeClient(){}

        webView.loadUrl("file:///android_asset/demo_ios.html")

        webView.registerHandler("submitFromWeb") { data, function ->
            Log.i("ios", "handler = submitFromWeb, data from web = $data")
            function.onCallBack("submitFromWeb exe, response data 中文 from Java")
        }

        val user = MainActivity.User()
        val location = MainActivity.Location()
        location.address = "SDU"
        user.location = location
        user.name = "大头鬼"

        webView.callHandler("functionInJs", Gson().toJson(user)) { data -> Log.i("ios", data) }

        webView.send("hello")

        button.setOnClickListener {
            webView.callHandler("functionInJs", "data from Java") { data ->
                Log.i("ios", "reponse data from js $data")
            }
        }
    }
}