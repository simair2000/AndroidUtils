package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;
import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.visitkorea.FacadeDetailCommon;
import com.simair.android.androidutils.openapi.visitkorea.FacadeImaegList;
import com.simair.android.androidutils.openapi.visitkorea.data.ImageListParam;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaDetailCommonObject;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaImageObject;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaLocationBasedListObject;
import com.simair.android.androidutils.ui.BaseActivity;
import com.simair.android.androidutils.ui.CirclePageIndicator;
import com.simair.android.androidutils.ui.PopupWait;

import org.json.JSONException;

import java.util.ArrayList;

public class TourGuideDetailActivity extends BaseActivity implements Command.CommandListener, View.OnClickListener {

    private static final String TAG = TourGuideDetailActivity.class.getSimpleName();
    private VisitKoreaLocationBasedListObject item;
    private TextView textTitle;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView textOverview;
    private TextView textHomepage;
    private Command commandDetail;
    private VisitKoreaDetailCommonObject detailCommon;
    private ArrayList<VisitKoreaImageObject> imageList;
    private CirclePageIndicator pageIndicator;

    public static Intent getIntent(Context context, VisitKoreaLocationBasedListObject item) {
        Intent i = new Intent(context, TourGuideDetailActivity.class);
        i.putExtra("item", item);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_guide_detail);
        Bundle extraData = getIntent().getExtras();
        if(extraData != null) {
            item = (VisitKoreaLocationBasedListObject)extraData.getSerializable("item");
        }
        initView();
    }

    private void initView() {
        textTitle = (TextView)findViewById(R.id.textTitle);
        textTitle.setText(item.getTitle());

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        pageIndicator = (CirclePageIndicator)findViewById(R.id.pageIndicator);
        pageIndicator.setViewPager(viewPager);

        textOverview = (TextView)findViewById(R.id.textOverview);
        textHomepage = (TextView)findViewById(R.id.textHomepage);

        if(item != null) {
            requestDetailInfo();
        }

        findViewById(R.id.btnNavi).setOnClickListener(this);
    }

    private void requestDetailInfo() {
        commandDetail = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                detailCommon = FacadeDetailCommon.getInstance(context).get(item.getContentId());
                ImageListParam param = new ImageListParam();
                param.setContentId(item.getContentId());
                param.setContentTypeId(item.getContentTypeId());
                imageList = FacadeImaegList.getInstance(context).get(param);
            }
        }.setOnCommandListener(this).showWaitDialog(this, PopupWait.getPopupView(this, false)).start();
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        textTitle.setText(detailCommon.getTitle());
        viewPagerAdapter.setList(imageList);

        if(!TextUtils.isEmpty(detailCommon.getOverview())) {
            textOverview.setText(Html.fromHtml(detailCommon.getOverview()));
        }
        if(!TextUtils.isEmpty(detailCommon.getHomePage())) {
            textHomepage.setText(Html.fromHtml(detailCommon.getHomePage()));
        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {

    }

    @Override
    public void onProgressUpdated(Command command, Bundle data) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNavi:
                startKakaoNavi();
                break;
        }
    }

    private void startKakaoNavi() {
        Log.e(TAG, "Keyhash : " + Utils.getKeyHash(this));
        Location destination = Location.newBuilder(item.getTitle(), item.getLongitude(), item.getLatitude()).build();
        NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();
        KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
        KakaoNaviParams params = builder.build();
        KakaoNaviService.getInstance().navigate(this, params);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<VisitKoreaImageObject> list;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = list.get(position).getImgUrl();
            return TourGuideImageFragment.newInstance(url);
        }

        public void setList(ArrayList<VisitKoreaImageObject> imageList) {
            list = imageList;
            notifyDataSetChanged();
        }
    }
}
