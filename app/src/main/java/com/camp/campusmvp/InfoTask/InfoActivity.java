package com.camp.campusmvp.InfoTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.camp.campusmvp.R;
import com.camp.campusmvp.data.StudentInfo.DataBean.GetDataResponseBean.ReturnBean.BodyBean.ItemsBean;
import com.camp.campusmvp.data.StudentInfoSort;

import java.lang.reflect.Field;

import static android.view.View.GONE;

public class InfoActivity extends AppCompatActivity {
    private RecyclerView recycler;

    public RecyclerAdapter getAdapter() {
        return adapter;
    }

    private RecyclerAdapter adapter;

    public RecyclerView getRecycler() {
        return recycler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolbar.setTitleTextAppearance(this, R.style.TitleText);
        toolbar.setTitle(getIntent().getExtras().getString(getResources().getString(R.string.TITLE)));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0,android.R.anim.slide_out_right);
            }
        });
        recycler = (RecyclerView) findViewById(R.id.recyclerview);

        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter = new RecyclerAdapter(this);
        recycler.setAdapter(adapter);

        InfoPresenter presenter = new InfoPresenter(this);
        presenter.doTask();

    }

    class RecyclerAdapter extends RecyclerView.Adapter<Holder> {
        private Context context;
        private ItemsBean info;
        private String[] title = {"??????","??????","??????","??????","?????????","????????????",
        "????????????","??????","??????","??????"};
        private String[] content = {"??????","??????","??????","??????","?????????","????????????",
                "????????????","??????","??????","??????"};
        private Field[] fields = StudentInfoSort.get();

        public RecyclerAdapter(Context context){this.context = context;}
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.info_listview_item,null);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            v.setBackgroundColor(getResources().getColor(R.color.GREY_300));
                            break;
                        case  MotionEvent.ACTION_CANCEL:
                            v.setBackgroundColor(Color.WHITE);
                            break;
                        case  MotionEvent.ACTION_UP:
                            v.setBackgroundColor(Color.WHITE);
                            break;
                    }
                    return true;
                }
            });
            if(info == null){
            holder.getTitle().setText(title[position]);
            holder.getContent().setText(content[position]);}
            else {

                if(fields[position].getType() == String.class)
                {
                    fields[position].setAccessible(true);
                    try {
                        holder.getContent().setText(fields[position].get(info).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                holder.getTitle().setText(title[position]);
            }
            if(position == title.length - 1){holder.getDivider().setVisibility(GONE);}
        }

        @Override
        public int getItemCount() {
            return title.length;
        }

        public void setData(ItemsBean info){this.info = info;}
    }

    class Holder extends RecyclerView.ViewHolder{

        public TextView getTitle() {
            return title;
        }

        private TextView title;

        public TextView getContent() {
            return content;
        }

        private TextView content;

        public TextView getDivider() {
            return divider;
        }

        private TextView divider;
        public Holder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            divider = (TextView) itemView.findViewById(R.id.divider);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,android.R.anim.slide_out_right);
    }
}
