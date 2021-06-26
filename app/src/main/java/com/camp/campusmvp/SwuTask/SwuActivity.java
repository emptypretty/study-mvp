package com.camp.campusmvp.SwuTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.camp.campusmvp.R;
import com.camp.campusmvp.TaskManager;
import com.camp.campusmvp.utils.Hlog;
import com.camp.campusmvp.view.ProgressView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2021/6/26.
 */

public class SwuActivity extends AppCompatActivity {
    public static final String GREEN ="com.camp.campusmvp.LOGIN_SUCCESS";
    public static final String WATER ="com.camp.campusmvp.LOGOUT_SUCCESS";

    @InjectView(R.id.progress)
    ProgressView progress;
    @InjectView(R.id.tabs)
    RelativeLayout tabs;
    @InjectView(R.id.swu_listview)
    ListView swuListview;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.main_content)
    RelativeLayout mainContent;


    private SwuPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swu_activity);
        ButterKnife.inject(this);

        progress.Clickable(true);

        presenter = new SwuPresenter(SwuActivity.this);
        presenter.setView(progress);


        ListViewAdapter adapter = new ListViewAdapter(getApplicationContext());

        swuListview.setAdapter(adapter);

        swuListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.showFragment(position);
            }
        });

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!SwuFlags.WATER){
                    SwuFlags.WATER = true;
                    refreshState();
                    presenter.logTask();
                }
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public void refreshState()
    {
        if(SwuFlags.GREEN)
        {
            tabs.setBackgroundColor(getResources().getColor(R.color.GREEN));
        }else
        {
            tabs.setBackgroundColor(getResources().getColor(R.color.GREY_900));
        }
        if(SwuFlags.WATER)
        {
            progress.setDrawsin(true);
        }
        else
        {
            progress.setDrawsin(false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshState();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskManager.getInstance().shutDownPool();
        Hlog.i("SWU","onDestroy");


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
