package com.camp.campusmvp.LogTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.camp.campusmvp.LogConstants;
import com.camp.campusmvp.MyApp;
import com.camp.campusmvp.R;
import com.camp.campusmvp.SwuTask.SwuFlags;
import com.camp.campusmvp.TaskManager;
import com.camp.campusmvp.swuwifi.LoginBean;
import com.camp.campusmvp.swuwifi.LogoutBean;
import com.camp.campusmvp.utils.Hlog;

import static com.camp.campusmvp.LogConstants.EX_COUNT;
import static com.camp.campusmvp.LogConstants.HAS_COUNT;
import static com.camp.campusmvp.LogConstants.LOGIN_SUCCESS_STR;
import static com.camp.campusmvp.LogConstants.PASSWORD;
import static com.camp.campusmvp.LogConstants.USERNAME;

/**
 * Created by Administrator on 2021/6/26.
 */

public class LogPresenter {

    public static final String TAG = "LogPresenter ";
    private Handler mHandler;
    private TaskManager taskManager;
    private View view;
    private LogActivity activity;
    private ProgressDialog progressDialog;
    private SharedPreferences sp;

    private LoginBean login_bean;
    private LoginBean bean;

    private AlertDialog dialog_valid;
    private AlertDialog dialog_haslog;

    private EditText editText;
    private ImageView imageView;

    private String musername;
    private String mpassword;

    public LogPresenter(Context context) {
        bean = new LoginBean();
        activity = (LogActivity) context;
        taskManager = TaskManager.getInstance();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                activity.sendBroadcast(new Intent(LogActivity.TASK_DONE));
                if (progressDialog != null) {
                    progressDialog.cancel();
                }
                if (msg.what == LogConstants.NETWORK_ERROR) {
                    snackbar(LogConstants.NETWORK_ERROR_STR);
                } else {
                    Bundle bundle = msg.getData();
                    /**??????handler??????ui????????????*/
                    switch (bundle.getInt(LogConstants.MODE)) {
                        case LogConstants.LOG_IN:
                            login_bean = (LoginBean) bundle.getSerializable(LogConstants.RESULT);
                            if (login_bean == null) {
                                snackbar(LogConstants.NETWORK_ERROR_STR);
                            } else {
                                loginResult(login_bean);
                            }
                            break;
                        case LogConstants.LOG_OUT:
                            logoutResult((LogoutBean) bundle.getSerializable(LogConstants.RESULT));
                            break;
                        case LogConstants.VALIDCODE:
                            Bitmap bitmap = bundle.getParcelable(LogConstants.RESULT);
                            validcodeResult(bitmap);
                            break;
                    }
                }
            }
        };
    }

    public void logTask(View view, String username, String password, int mode) {

        this.view = view;
        musername = username;
        mpassword = password;
        sp = activity.getSharedPreferences(MyApp.SPREF,0);
        taskManager.setHander(mHandler);
        switch (mode) {
            case LogConstants.LOG_IN:
                /**???????????????*/
                if (login_bean != null && login_bean.getValidCodeUrl() != null && login_bean.getValidCodeUrl().length() > 3) {
                    bean.setValidCodeUrl(activity.getString(R.string.HTTP_SWU) + login_bean.getValidCodeUrl());
                    taskManager.getBitmap(bean.getValidCodeUrl());
                } else {
                    taskManager.login(username, password, "");
                }
                break;
            case LogConstants.LOG_OUT:
                taskManager.logout_all(username, password);
                break;
        }
    }

    /**
     * ??????????????????????????????
     */
    private void loginResult(LoginBean result) {
        if (result.getReslult() != null && result.getReslult().equals(activity.getString(R.string.SUCCESS))) {
            saveUser();
        } else {
            loginFail(result);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param result ??????????????????????????????????????????1???????????????0???????????????-1????????????
     */
    private void logoutResult(LogoutBean result) {
        if (result.getResult().equals("success")) {
            snackbar(result.getMessage());

            if( musername.equals(sp.getString(USERNAME,"")) )
            {
                SwuFlags.WATER = false;
                SwuFlags.GREEN = false;
            }
        } else {
            snackbar(result.getMessage());
        }
    }

    private void validcodeResult(final Bitmap bitmap) {
        showValidDialog(activity,bitmap);
    }

    private void snackbar(String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_LONG).show();
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     */
    private void saveUser() {
        SwuFlags.HAS_LOGED = true;
        SwuFlags.GREEN = true;
        SwuFlags.WATER = false;
        snackbar(LOGIN_SUCCESS_STR);
        SharedPreferences sp = activity.getSharedPreferences(MyApp.SPREF, 0);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(USERNAME, musername);
        editor.putString(PASSWORD, mpassword);
        editor.putBoolean(HAS_COUNT, true);
        editor.putBoolean(EX_COUNT, true);
        editor.commit();

        activity.finish();
    }

    /**
     * ??????????????????
     */
    private void loginFail(LoginBean result) {
        if ((result.getMessage().contains(LogConstants.HASLOGED_OTHER_PLACE) || result.getMessage().contains(LogConstants.USERS_LIMITED))) {
            showHas(activity);
        } else if (result.getMessage().contains(activity.getString(R.string.VALIDCODE_ERROR))) {
            editText.setText("");
            editText.setHintTextColor(activity.getResources().getColor(R.color.RED));
            editText.setHint("???????????????");
            taskManager.getBitmap(bean.getValidCodeUrl());
        } else {
            if(editText != null)
            {
                editText.setText("");
                editText.setHintTextColor(Color.GRAY);
                editText.setHint("???????????????");
            }
            Hlog.i(TAG, result.getMessage());
            snackbar(result.getMessage());
        }
    }

    private void showValidDialog(Context context,Bitmap bitmap) {

        AlertDialog.Builder builder = null;

        if(dialog_valid == null) {
            if (builder == null) {builder = new AlertDialog.Builder(context);}
            builder.setMessage("?????????");
            builder.setCancelable(false);
            View view = activity.getLayoutInflater().inflate(R.layout.validvode_dialog, null, false);
            editText = (EditText) view.findViewById(R.id.validvode_edittext);
            imageView = (ImageView) view.findViewById(R.id.validvode_bitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar(activity.getString(R.string.REFRESH_VALIDCODE));
                    taskManager.getBitmap(bean.getValidCodeUrl());
                }});
            imageView.setImageBitmap(bitmap);
            builder.setView(view);

            builder.setPositiveButton(activity.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.sendBroadcast(new Intent(LogActivity.TASK_DONE));
                    dialog.cancel();
                }
            });
            builder.setNegativeButton(activity.getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    activity.sendBroadcast(new Intent(LogActivity.TASK_START));
                    taskManager.login(musername,mpassword,editText.getText().toString().trim());
                }});
            dialog_valid = builder.create();
            dialog_valid.show();
        }
        else
        {
            imageView.setImageBitmap(bitmap);
            dialog_valid.show();
        }
    }

    public void showHas(Context context) {
        /**dialog_haslog = null ?????????*/
        if (dialog_haslog == null) {

            AlertDialog.Builder builder = null;
            if (builder == null) {
                builder = new AlertDialog.Builder(context);
            }
            builder.setMessage(context.getString(R.string.HAS_LOGED_OTHER_PLACE));
            builder.setCancelable(false);
            builder.setPositiveButton(activity.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(activity.getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage(activity.getString(R.string.OUTING));
                    progressDialog.setCancelable(true);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    taskManager.logout_all(musername, mpassword);
                }
            });
            dialog_haslog = builder.create();
            dialog_haslog.show();
        }
        else
        {

            dialog_haslog.show();
        }
    }

}
