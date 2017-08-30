package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.kakao.APISearch;
import com.simair.android.androidutils.openapi.kakao.data.ImageSearchResult;
import com.simair.android.androidutils.openapi.kakao.data.VideoSearchResult;
import com.simair.android.androidutils.openapi.kakao.data.WebSearchResult;
import com.simair.android.androidutils.ui.CustomPopup;
import com.simair.android.androidutils.ui.PopupWait;

import org.json.JSONException;

public class DaumSearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener,
        Command.CommandListener,
        WebSearchFragment.WebSearchFragmentListener,
        VideoSearchFragment.VideoSearchFragmentListener,
        ImageSearchFragment.ImageSearchFragmentListener
{

    private static final int MAX_COUNT = 10;
    private static final String TAG = DaumSearchActivity.class.getSimpleName();
    private EditText editSearch;
    private Command commandSearch;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private Command commandVideoSearch;
    private Command commandImageSearch;
    private CustomPopup waitPopup;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, DaumSearchActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_search);
        initProperties();
        initView();
    }

    private void initProperties() {

    }

    private void initView() {
        editSearch = (EditText)findViewById(R.id.editSearch);
        editSearch.setOnEditorActionListener(this);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(7);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        DaumSearchTab[] tabs = DaumSearchTab.values();
        for(DaumSearchTab tab : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tab.title));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(!TextUtils.isEmpty(editSearch.getText())) {
            waitPopup = PopupWait.getPopupView(this, true).show();
            requestSearch(1);
            requestVideoSearch(1);
            requestImageSearch(1);
            return true;
        }
        return false;
    }

    private void requestSearch(final int page) {
        Utils.hideKeyboard(this);
        commandSearch = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                String keyword = editSearch.getText().toString();
                WebSearchResult webResult = APISearch.getInstance().requestWebSearch(keyword, APISearch.SortParam.recency, page, MAX_COUNT);
                data.putSerializable("webResult", webResult);
                data.putInt("page", page);
            }
        }.setOnCommandListener(this).start();
    }

    private void requestVideoSearch(final int page) {
        Utils.hideKeyboard(this);
        commandVideoSearch = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                String keyword = editSearch.getText().toString();
                VideoSearchResult videoResult = APISearch.getInstance().requestVideoSearch(keyword, APISearch.SortParam.recency, page, MAX_COUNT);
                data.putSerializable("videoResult", videoResult);
                data.putInt("page", page);
            }
        }.setOnCommandListener(this).start();
    }

    private void requestImageSearch(final int page) {
        Utils.hideKeyboard(this);
        commandImageSearch = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                String keyword = editSearch.getText().toString();
                ImageSearchResult result = APISearch.getInstance().requestImageSearch(keyword, APISearch.SortParam.recency, page, MAX_COUNT * 3);
                data.putSerializable("result", result);
                data.putInt("page", page);
            }
        }.setOnCommandListener(this).start();
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        CustomPopup.hideDialog(waitPopup);
        if(command == commandSearch) {
            WebSearchResult webResult = (WebSearchResult) data.getSerializable("webResult");
            int page = data.getInt("page", 1);
            WebSearchFragment fragment = (WebSearchFragment) DaumSearchTab.getInstance(DaumSearchTab.WEB_SEARCH_TAB.ordinal()).fragment;
            fragment.setData(webResult, page);
        } else if(command == commandVideoSearch) {
            VideoSearchResult result = (VideoSearchResult) data.getSerializable("videoResult");
            int page = data.getInt("page", 1);
            VideoSearchFragment fragment = (VideoSearchFragment) DaumSearchTab.getInstance(DaumSearchTab.VIDEO_SEARCH_TAB.ordinal()).fragment;
            fragment.setData(result, page);
        } else if(command == commandImageSearch) {
            ImageSearchResult result = (ImageSearchResult) data.getSerializable("result");
            int page = data.getInt("page", 1);
            ImageSearchFragment fragment = (ImageSearchFragment) DaumSearchTab.getInstance(DaumSearchTab.IMAGE_SEARCH_TAB.ordinal()).fragment;
            fragment.setData(result, page);
        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {
        CustomPopup.hideDialog(waitPopup);
        Log.e(TAG, "onFail - " + errorMessage);
    }

    @Override
    public void onLoadMore(WebSearchFragment fragment, int page, int totalItemsCount) {
        requestSearch(page + 1);
    }

    @Override
    public void onLoadMore(VideoSearchFragment fragment, int page, int total) {
        requestVideoSearch(page + 1);
    }

    @Override
    public void onLoadMore(ImageSearchFragment fragment, int page) {
        requestImageSearch(page + 1);
    }

    public enum DaumSearchTab {
        WEB_SEARCH_TAB("웹", WebSearchFragment.newInstance(null)),
        VIDEO_SEARCH_TAB("동영상", VideoSearchFragment.newInstance(null)),
        IMAGE_SEARCH_TAB("이미지", ImageSearchFragment.newInstance(null)),
        BLOG_SEARCH_TAB("블로그", TourGuideImageFragment.newInstance(null)),
        TIP_SEARCH_TAB("팁", TourGuideImageFragment.newInstance(null)),
        BOOK_SEARCH_TAB("책", TourGuideImageFragment.newInstance(null)),
        CAFE_SEARCH_TAB("카페", TourGuideImageFragment.newInstance(null)),
        ;

        public String title;
        public Fragment fragment;

        DaumSearchTab(String title, Fragment f) {
            this.title = title;
            this.fragment = f;
        }


        public static DaumSearchTab getInstance(int ordinal) {
            if(ordinal == WEB_SEARCH_TAB.ordinal()) {
                return WEB_SEARCH_TAB;
            } else if(ordinal == VIDEO_SEARCH_TAB.ordinal()) {
                return VIDEO_SEARCH_TAB;
            } else if(ordinal == IMAGE_SEARCH_TAB.ordinal()) {
                return IMAGE_SEARCH_TAB;
            } else if(ordinal == BLOG_SEARCH_TAB.ordinal()) {
                return BLOG_SEARCH_TAB;
            } else if(ordinal == TIP_SEARCH_TAB.ordinal()) {
                return TIP_SEARCH_TAB;
            } else if(ordinal == BOOK_SEARCH_TAB.ordinal()) {
                return BOOK_SEARCH_TAB;
            } else if(ordinal == CAFE_SEARCH_TAB.ordinal()) {
                return CAFE_SEARCH_TAB;
            }
            return null;
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DaumSearchTab.getInstance(position).fragment;
        }

        @Override
        public int getCount() {
            return DaumSearchTab.values().length;
        }
    }
}
