package com.camp.campusmvp.http;

/**
 * Created by Administrator on 2021/6/26.
 */

public class Params {

    private static Params params = new Params();

    private Params(){

    }

    public static Params getInstance(){
        return params;
    }

    private String url = null;
    private String from = null;//请求提交的表单

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
