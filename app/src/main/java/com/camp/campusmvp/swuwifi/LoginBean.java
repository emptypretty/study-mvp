package com.camp.campusmvp.swuwifi;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Administrator on 2021/6/26.
 */

public class LoginBean implements Serializable {

    private String userIndex;
    private String reslult;
    private String message;
    private int keepaliveInterval;
    private String validCodeUrl;

    public static LoginBean objectFromData(String str){
        return new Gson().fromJson(str,LoginBean.class);
    }

    public String getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(String userIndex) {
        this.userIndex = userIndex;
    }

    public String getReslult() {
        return reslult;
    }

    public void setReslult(String reslult) {
        this.reslult = reslult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getKeepaliveInterval() {
        return keepaliveInterval;
    }

    public void setKeepaliveInterval(int keepaliveInterval) {
        this.keepaliveInterval = keepaliveInterval;
    }

    public String getValidCodeUrl() {
        return validCodeUrl;
    }

    public void setValidCodeUrl(String validCodeUrl) {
        this.validCodeUrl = validCodeUrl;
    }
}
