package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.openapi.kakao.data.ImageSearchDocument;
import com.simair.android.androidutils.ui.CirclePageIndicator;
import com.simair.android.androidutils.ui.PageIndicator;

import java.io.Serializable;
import java.util.List;

public class ImageGalleryActivity extends AppCompatActivity {

    private List<ImageSearchDocument> list;
    private int position;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private CirclePageIndicator indicator;

    static public Intent getIntent(Context context, List<ImageSearchDocument> list, int position) {
        Intent i = new Intent(context, ImageGalleryActivity.class);
        i.putExtra("position", position);
        i.putExtra("list", (Serializable) list);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        position = getIntent().getIntExtra("position", 0);
        list = (List<ImageSearchDocument>)getIntent().getSerializableExtra("list");
        initView();
    }

    private void makeLinkInformation(int position) {
        final ImageSearchDocument item = viewPagerAdapter.getItem(position);
        ((TextView)findViewById(R.id.textSource)).setText(item.getSource());
        TextView textUrl = (TextView) findViewById(R.id.textUrl);
        textUrl.setText(item.getSourceUrl());
        textUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = item.getSourceUrl();
                Utils.startWebBrowser(ImageGalleryActivity.this, url);
            }
        });
    }

    private void initView() {
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(position);

        indicator = (CirclePageIndicator)findViewById(R.id.pageIndicator);
        indicator.setViewPager(viewPager);

        makeLinkInformation(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                makeLinkInformation(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private final LayoutInflater inflater;

        public ViewPagerAdapter() {
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public ImageSearchDocument getItem(int position) {
            return list.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final ImageSearchDocument item = getItem(position);
            View view = inflater.inflate(R.layout.image_view, null);

            ImageView imgView = (ImageView) view.findViewById(R.id.imageView);
            Glide.with(ImageGalleryActivity.this).load(Uri.parse(item.getImageUrl())).into(imgView);
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.startWebBrowser(ImageGalleryActivity.this, item.getSourceUrl());
                }
            });

            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

}
