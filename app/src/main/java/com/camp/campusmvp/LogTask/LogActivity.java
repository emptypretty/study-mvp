package com.camp.campusmvp.LogTask;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.camp.campusmvp.LogConstants;
import com.camp.campusmvp.MyApp;
import com.camp.campusmvp.R;

import static com.camp.campusmvp.LogConstants.PASSWORD;
import static com.camp.campusmvp.LogConstants.USERNAME;


/**
 * Created by Administrator on 2021/6/26.
 */

public class LogActivity extends AppCompatActivity {

    public static final String TASK_DONE= "com.camp.campusmvp.LogActivity.TASK_DONE";
    public static final String TASK_START= "com.camp.campusmvp.LogActivity.TASK_START";
    private String title;
    private String button;
    private int mode;

    private AutoCompleteTextView username;
    private EditText password;
    private Button mbutton;

    public ScrollView getLogin_form() {return login_form;}
    private ScrollView login_form;


    public LinearLayout getProgress() {return progress;}
    private LinearLayout progress;

    private String s_username;
    private String s_password;
    private LogPresenter presenter;

    private LogActivityReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new LogPresenter(LogActivity.this);
        SharedPreferences sp = getSharedPreferences(MyApp.SPREF, 0);

        mode = getIntent().getExtras().getInt(getString(R.string.MODE));
        title = getIntent().getExtras().getString(getString(R.string.TITLE));
        button = getIntent().getExtras().getString(getString(R.string.BUTTON));
        setContentView(R.layout.log_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setTitleTextAppearance(this,R.style.TitleText);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        progress = (LinearLayout) findViewById(R.id.progessbar);
        login_form = (ScrollView) findViewById(R.id.login_form);

        if (sp.getBoolean(LogConstants.HAS_COUNT, false)) {
            username.setText(sp.getString(USERNAME, ""));
            password.setText(sp.getString(PASSWORD, ""));
        }

        mbutton = (Button) findViewById(R.id.email_sign_in_button);
        mbutton.setText(button);

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check())
                {
                    sendBroadcast(new Intent(TASK_START));
                    presenter.logTask(v, s_username, s_password, mode);
                }
            }
        });

    }

    private boolean check() {
        if (username.getText().toString().trim().length() == 0) {
            Snackbar.make(username,"???????????????",Snackbar.LENGTH_SHORT).show();
            username.requestFocus();
            return false;
        }
        if (password.getText().toString().trim().length() == 0) {
            Snackbar.make(password,"???????????????", Snackbar.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }
        s_username = username.getText().toString().trim();
        s_password = password.getText().toString().trim();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(TASK_DONE);
        filter.addAction(LogActivity.TASK_START);
        receiver = new LogActivityReceiver();
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


}
