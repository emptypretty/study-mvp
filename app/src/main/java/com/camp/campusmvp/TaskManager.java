package com.camp.campusmvp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.camp.campusmvp.InfoTask.InfoPresenter;
import com.camp.campusmvp.data.CXParams;
import com.camp.campusmvp.data.StudentCj;
import com.camp.campusmvp.data.StudentInfo.DataBean.GetDataResponseBean.ReturnBean.BodyBean.ItemsBean;
import com.camp.campusmvp.data.remote.StudentSourceRemote;
import com.camp.campusmvp.http.HjzHttp;
import com.camp.campusmvp.http.Params;
import com.camp.campusmvp.swuwifi.LoginBean;
import com.camp.campusmvp.swuwifi.LogoutBean;
import com.camp.campusmvp.swuwifi.Remote.SwuWifiLandTask;
import com.camp.campusmvp.transcripttask.TranscriptPresenter;
import com.camp.campusmvp.utils.FileUtil;
import com.camp.campusmvp.utils.Hlog;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2021/6/26.
 */

public class TaskManager {

    private static final String TAG = "TaskManager";
    private static TaskManager ourInstance = new TaskManager();

    public static TaskManager getInstance() {
        return ourInstance;
    }

    private TaskManager() {
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        pool.setCorePoolSize(4);
        pool.setKeepAliveTime(20, TimeUnit.SECONDS);
    }

    /**
     * 类成员
     */
    private ThreadPoolExecutor pool;

    private Handler mHander;

    public void setHander(Handler mHander) {
        this.mHander = mHander;
    }

    /**
     * 账号登陆的方法
     */
    public void login(final String username, final String password, final String validcode) {

        pool.submit(new Runnable() {
            @Override
            public void run() {

                Message message = new Message();
                Bundle bundle = new Bundle();
                LoginBean result;
                try {
                    result = SwuWifiLandTask.getInstance().login(username, password, validcode);
                    bundle.putSerializable(LogConstants.RESULT, result);
                    bundle.putInt(LogConstants.MODE, LogConstants.LOG_IN);
                    message.setData(bundle);
                    mHander.sendMessage(message);
                } catch (Exception e) {
                    Hlog.i(TAG, "login()" + e.getMessage());
                    mHander.sendEmptyMessage(LogConstants.NETWORK_ERROR);
                }
            }
        });
    }

    /**
     * 账号退出方法
     */
    public void logout(final String userid) {

        pool.submit(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                LogoutBean result;
                try {
                    result = SwuWifiLandTask.getInstance().logout(userid);
                    bundle.putSerializable(LogConstants.RESULT, result);
                    bundle.putInt(LogConstants.MODE, LogConstants.LOG_OUT);
                    message.setData(bundle);
                    mHander.sendMessageDelayed(message, 1000);

                } catch (Exception e) {
                    Hlog.i("TAG", ".logout()" + e.getMessage());
                    mHander.sendEmptyMessage(LogConstants.NETWORK_ERROR);
                }
            }
        });
    }

    /**
     * 账号退出方法
     */
    public void logout_all(final String username, final String password) {

        pool.submit(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                LogoutBean bean = new LogoutBean();

                String result;
                try {
                    result = SwuWifiLandTask.getInstance().logout_all(username, password);
                    bean.setResult(result);
                    if (result.contains("success")) {
                        bean.setMessage(LogConstants.LOGOUT_SUCCESS_STR);
                        bundle.putSerializable(LogConstants.RESULT, bean);
                    } else {
                        bean.setMessage(LogConstants.LOGOUT_FAIL_STR);
                        bundle.putSerializable(LogConstants.RESULT, bean);
                    }
                    bundle.putInt(LogConstants.MODE, LogConstants.LOG_OUT);
                    message.setData(bundle);
                    mHander.sendMessage(message);

                } catch (Exception e) {
                    Hlog.i("TAG", ".logout()" + e.getMessage());
                    mHander.sendEmptyMessage(LogConstants.NETWORK_ERROR);
                }
            }
        });
    }

    public void getStudentCj(final Context context, final String username, final String password) {

        pool.submit(new Runnable() {
            @Override
            public void run() {
                CXParams cxparams = new CXParams();
                cxparams.setUsename(username);
                cxparams.setPassword(password);
                StudentSourceRemote remote = StudentSourceRemote.getInstance();
                StudentCj cj;
                Bundle bundle = new Bundle();
                Message message = new Message();
                try {
                    //先获取学号
                    ItemsBean info = remote.getStudentInfo(cxparams);
                    //有了学号再查询成绩
                    cj = remote.getTranscript(cxparams);

                    /**保存成绩对象*/
                    FileUtil<StudentCj> fileUtil = new FileUtil<>();
                    FileUtil<ItemsBean> fileUtil1 = new FileUtil<>();
                    fileUtil.save(context, cj, "cj");
                    fileUtil1.save(context, info, "info");

                    bundle.putSerializable(InfoPresenter.INFO, info);
                    bundle.putSerializable(TranscriptPresenter.STUDENTCJ, cj);

                    /**返回1表示获取到数据*/
                    bundle.putInt(TranscriptPresenter.RESULT, 1);

                    message.setData(bundle);
                    mHander.sendMessage(message);
                } catch (Exception e) {
                    /**返回-1表示网络连接失败*/
                    Hlog.i("SWU", "ex-file=" + e.getMessage());
                    bundle.putInt(TranscriptPresenter.RESULT, -1);

                    message.setData(bundle);
                    mHander.sendMessage(message);
                }
            }
        });
    }

    /**
     * 网络测试
     */
    public void NetTest() {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                Params params = Params.getInstance();
                params.setUrl("http://www.baidu.com");
                InputStream in = null;
                try {
                    in = HjzHttp.getInstance().get(params);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("pass", true);
                    //bundle.putInt(SwuPresenter.MODE,LogConstants.NETWORK_TEST);
                    message.setData(bundle);
                    mHander.sendMessage(message);
                } catch (Exception e) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("pass", false);
                    //bundle.putInt(SwuPresenter.MODE,SwuPresenter.LOGIN_TEST);
                    message.setData(bundle);
                    mHander.sendMessage(message);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void getBitmap(final String url) {
        pool.submit(new Runnable() {
            @Override
            public void run() {
                Params params = Params.getInstance();
                params.setUrl(url);
                params.setFrom("");
                InputStream in = null;
                try {
                    in = HjzHttp.getInstance().get(params);
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(LogConstants.RESULT, bitmap);
                    bundle.putInt(LogConstants.MODE, LogConstants.VALIDCODE);
                    Message message = new Message();
                    message.setData(bundle);
                    mHander.sendMessage(message);
                } catch (Exception e) {
                    mHander.sendEmptyMessageDelayed(LogConstants.NETWORK_ERROR, 1000);
                }
            }
        });
    }

    public void shutDownPool() {
        pool.shutdown();
    }


}
