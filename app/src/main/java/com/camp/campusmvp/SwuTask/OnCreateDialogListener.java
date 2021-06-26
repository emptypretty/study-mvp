package com.camp.campusmvp.SwuTask;

import android.app.AlertDialog;

/**
 * Created by Administrator on 2021/6/26.
 */

public interface OnCreateDialogListener {

    String getContent();
    void onOk();
    void add(AlertDialog.Builder builder);

}
