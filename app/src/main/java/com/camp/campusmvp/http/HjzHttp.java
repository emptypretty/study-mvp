package com.camp.campusmvp.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2021/6/26.
 */

public class HjzHttp implements IHjzHttp {

    private StringBuilder cookie = new StringBuilder();
    private static HjzHttp ourinstance = new HjzHttp();

    public HjzHttp setCookie(String cookie) {
        this.cookie.append(cookie);
        return ourinstance;
    }

    //实现HjzHttp对象的管理（类似线程池），单例模式存在线程安全问题，加锁会影响多线程性能
    public static HjzHttp getInstance() {
        return ourinstance;
    }

    private HjzHttp(){
        HttpURLConnection.setFollowRedirects(false);
    }

    @Override
    public InputStream post(Params params) throws Exception {

        OutputStream os= null;
        InputStream in =null;



        try{

            URL url = new URL(params.getUrl());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
        /*判断请求是否需要cookie，需要在setRequestMethod("POST")之前*/

            if(cookie.length() != 0){
                con.setRequestProperty("Cookie",cookie.toString());
            }

            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestMethod("POST");
            con.setReadTimeout(20 * 1000);
            con.setConnectTimeout(20 * 1000);

            con.setDoOutput(true);
            con.setDoInput(true);

            con.connect();
            os = con.getOutputStream();
            if (params.getFrom() != null) {
                os.write(params.getFrom().getBytes());
            }

            if (con.getResponseCode() == 200) {

                if(con.getHeaderFields().get("Set-Cookie") != null){
                    if(cookie.length() < 2){
                        cookie.append(con.getHeaderFields().get("Set-Cookie").get(0));
                    }
                    else {
                        cookie.append(";"+con.getHeaderFields().get("Set-Cookie").get(0));
                    }

                }
                in = con.getInputStream();
            }
        }finally {
            if(os != null){
                os.close();
            }
        }
        return in;
    }

    @Override
    public InputStream get(Params params) throws Exception {
        InputStream in = null;
        URL url = new URL(params.getUrl());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        /*判断请求是否需要cookie，需要在setRequestMethod("POST")之前*/
        if (cookie != null) {
            con.setRequestProperty("Cookie", cookie.toString());

        }

        //Mozilla/5.0 (Linux; Android 5.1.1; NX529J Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.68 Mobile Safari/537.36
        con.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; Android 5.1.1; NX529J Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.68 Mobile Safari/537.36");
        con.setRequestMethod("GET");
        con.setReadTimeout(20 * 1000);
        con.setConnectTimeout(20 * 1000);

        con.setDoInput(true);
        con.connect();

        if (con.getResponseCode() == 200 || con.getResponseCode() == 302) {
            in = con.getInputStream();
        }
        return in;
    }
}
