package com.github.lzyzsd.jsbridge.example;

import com.github.lzyzsd.jsbridge.BridgeData;

/**
 * Created by silen on 07/05/2018.
 */

public class SubmitModel extends BridgeData {
    private String user;
    private String pwd;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
