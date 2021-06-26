package com.camp.campusmvp.InfoTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.camp.campusmvp.MyApp;
import com.camp.campusmvp.SwuTask.SwuPresenter;
import com.camp.campusmvp.TaskManager;
import com.camp.campusmvp.transcripttask.TranscriptPresenter;
import com.camp.campusmvp.data.StudentCj;
import com.camp.campusmvp.data.StudentInfo.DataBean.GetDataResponseBean.ReturnBean.BodyBean.ItemsBean;
import com.camp.campusmvp.utils.FileUtil;

import static com.camp.campusmvp.LogConstants.EX_COUNT;
import static com.camp.campusmvp.LogConstants.PASSWORD;
import static com.camp.campusmvp.LogConstants.USERNAME;

/**
 * Created by huanjinzi on 2016/9/28.
 */

public class InfoPresenter {

    public static final String INFO = "InfoPresenter.info";
    private Context context;
    private Handler mHandler;
    private SharedPreferences sp;

    public InfoPresenter(final Context context){
        this.context = context;
        sp = context.getSharedPreferences(MyApp.SPREF, 0);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                switch (bundle.getInt("result")){
                    case 1:
                        ItemsBean info = (ItemsBean) bundle.getSerializable(InfoPresenter.INFO);
                        setData(info);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(TranscriptPresenter.HAS_TRANSCRIPT, true);
                        editor.putBoolean(EX_COUNT,false);
                        editor.commit();
                        break;
                }
            }
        };
    }

    public void doTask() {


        if(sp.getBoolean(EX_COUNT,false)){

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(EX_COUNT,false);
            editor.commit();

            String username = sp.getString(USERNAME, "");
            String password = sp.getString(PASSWORD, "");
            TaskManager task = TaskManager.getInstance();
            task.setHander(mHandler);
            task.getStudentCj(context,username, password);
        }
        else {

            if (sp.getBoolean(TranscriptPresenter.HAS_TRANSCRIPT, false) ) {
                FileUtil<ItemsBean> fileUtil = new FileUtil<>();
                ItemsBean info = (ItemsBean) fileUtil.get(context,"info");
                setData(info);

            } else {
                String username = sp.getString(USERNAME, "");
                String password = sp.getString(PASSWORD, "");
                TaskManager task = TaskManager.getInstance();
                task.setHander(mHandler);
                task.getStudentCj(context,username, password);
            }

        }


    }
    /***/
    private void setData(ItemsBean result){

        InfoActivity activity = (InfoActivity) context;
        RecyclerView view = activity.getRecycler();
        InfoActivity.RecyclerAdapter adapter = activity.getAdapter();
        adapter.setData(result);
        view.invalidate();
    }

}
