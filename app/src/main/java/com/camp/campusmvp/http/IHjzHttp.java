package com.camp.campusmvp.http;

import java.io.InputStream;

/**
 * Created by Administrator on 2021/6/26.
 */

public interface IHjzHttp {

    //post请求
    InputStream post(Params params) throws Exception;
    //get请求
    InputStream get(Params params) throws Exception;

}
