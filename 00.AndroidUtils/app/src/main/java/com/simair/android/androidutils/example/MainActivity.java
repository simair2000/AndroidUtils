package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.simair.android.androidutils.R;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private ListView listView;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initView();
    }

    private void initView() {
        listView = (ListView)findViewById(R.id.listView);
        listAdapter = new ListAdapter(this);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ListAdapter.ListItem item = listAdapter.getItem(i);
        Snackbar.make(view, item.name(), Snackbar.LENGTH_SHORT).show();
        if(item.intent == null) {
            Snackbar.make(view, "아직 작업 중입니다.", Snackbar.LENGTH_SHORT).show();
        } else {
            startActivity(item.intent);
        }
    }

    public static class ListAdapter extends BaseAdapter {

        public enum ListItem {
            HTTP_GET_TEST(HttpActivity.getIntent(context)),
            DOWNLOAD_TEST(DownloadActivity.getIntent(context)),
            UI_TEST(UIExamActivity.getIntent(context)),
            FORE_CAST_TEST(WeatherForecastActivity.getIntent(context)),
            ;

            private final Intent intent;

            ListItem(Intent intent) {
                this.intent = intent;
            }
        }

        private static Context context = null;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ListItem.values().length;
        }

        @Override
        public ListItem getItem(int i) {
            return ListItem.values()[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            }
            ((TextView)view.findViewById(R.id.textView)).setText(ListItem.values()[i].name());
            return view;
        }
    }
}
