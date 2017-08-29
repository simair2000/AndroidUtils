package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.simair.android.androidutils.R;

import java.util.ArrayList;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuNavi:
                Toast.makeText(this, "내비게이션 설정", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuEtc:
                break;
        }
        return true;
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
//        Snackbar.make(view, item.name(), Snackbar.LENGTH_SHORT).show();
        if(item.intent == null) {
            Snackbar.make(view, "아직 작업 중입니다.", Snackbar.LENGTH_SHORT).show();
        } else {
            startActivity(item.intent);
        }
    }

    public static class ListAdapter extends BaseAdapter {

        public enum ListItem {
            HTTP_GET_TEST(HttpActivity.getIntent(context), R.drawable.if_bad_egg, "HTTP Get 테스트", false),
            DOWNLOAD_TEST(DownloadActivity.getIntent(context), R.drawable.if_happy, "파일 다운로드 테스트", false),
            UI_TEST(UIExamActivity.getIntent(context), R.drawable.if_victory, "UI관련 유틸 테스트", false),
            FORECAST_TEST(WeatherForecastActivity.getIntent(context), R.drawable.if_victory, "날씨 정보", true),
            VISIT_KOREA(VisitKoreaActivity.getIntent(context), R.drawable.if_happy, "관광 정보", true),
            DAUM_SEARCH(DaumSearchActivity.getIntent(context), R.drawable.if_amazing, "Daum 검색", true),
            ;

            private final Intent intent;
            private final int iconRes;
            private final String title;
            private final boolean show;

            ListItem(Intent intent, int iconRes, String title, boolean show) {
                this.intent = intent;
                this.iconRes = iconRes;
                this.title = title;
                this.show = show;
            }

            public static ArrayList<ListItem> getShowingList() {
                ArrayList<ListItem> list = new ArrayList<>();
                for(ListItem item : ListItem.values()) {
                    if(item.show) {
                        list.add(item);
                    }
                }
                return list;
            }
        }

        private static Context context = null;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return ListItem.getShowingList().size();
        }

        @Override
        public ListItem getItem(int i) {
            return ListItem.getShowingList().get(i);
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
            ListItem item = getItem(i);
            ((ImageView)view.findViewById(R.id.imgIcon)).setImageResource(item.iconRes);
            ((TextView)view.findViewById(R.id.textView)).setText(item.title);
            return view;
        }
    }
}
