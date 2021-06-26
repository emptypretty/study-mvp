package com.camp.campusmvp.SwuTask;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2021/6/26.
 */

public interface ISwuPresenter {

    void showFragment(Context context,int position);
    void logTask(Context context,View v);
    void login();
    void logout();
}
