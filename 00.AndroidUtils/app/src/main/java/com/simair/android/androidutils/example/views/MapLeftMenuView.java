package com.simair.android.androidutils.example.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.simair.android.androidutils.R;

public class MapLeftMenuView extends RelativeLayout {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;



    public MapLeftMenuView(Context context) {
        this(context, null, 0);
    }

    public MapLeftMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapLeftMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_map_left_menu, this, true);
        initView();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("카테고리별"));
        tabLayout.addTab(tabLayout.newTab().setText("검색"));

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    class PagerAdapter extends android.support.v4.view.PagerAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return false;
        }
    }
}
