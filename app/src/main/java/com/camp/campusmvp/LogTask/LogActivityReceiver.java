package com.camp.campusmvp.LogTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by Administrator on 2021/6/26.
 */

public class LogActivityReceiver extends BroadcastReceiver {


    private LogActivity activity;

    @Override
    public void onReceive(Context context, Intent intent) {
        activity = (LogActivity) context;
        switch (intent.getAction()) {
            case LogActivity.TASK_START:
                activity.getProgress().setVisibility(View.VISIBLE);
                activity.getLogin_form().setVisibility(View.INVISIBLE);
                break;
            case LogActivity.TASK_DONE:
                activity.getProgress().setVisibility(View.INVISIBLE);
                activity.getLogin_form().setVisibility(View.VISIBLE);
                break;
        }
    }


}
