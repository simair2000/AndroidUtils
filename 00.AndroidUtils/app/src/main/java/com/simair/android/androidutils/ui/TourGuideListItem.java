package com.simair.android.androidutils.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaLocationBasedListObject;

/**
 * Created by simair on 17. 7. 18.
 */

public class TourGuideListItem extends LinearLayout {
    private final Context context;
    private ImageView imgThumbnail;
    private TextView textTitle;
    private TextView textAddress;
    private TextView textContact;

    public TourGuideListItem(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.tour_guide_list_item, this, true);
        initView();
    }

    public TourGuideListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.tour_guide_list_item, this, true);
        initView();
    }

    public TourGuideListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.tour_guide_list_item, this, true);
        initView();
    }

    private void initView() {
        imgThumbnail = (ImageView)findViewById(R.id.imgThumbnail);
        textTitle = (TextView)findViewById(R.id.textTitle);
        textAddress = (TextView)findViewById(R.id.textAddress);
        textContact = (TextView)findViewById(R.id.textContact);
    }

    public void setItem(VisitKoreaLocationBasedListObject item) {
        setDefault();
        if(item != null) {
            Glide.with(context).load(item.getThumbnailURL()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgThumbnail);
            if(!TextUtils.isEmpty(item.getTitle())) {
                textTitle.setText(item.getTitle());
            }
            if(!TextUtils.isEmpty(item.getAddress())) {
                textAddress.setText(item.getAddress());
            }
            if(!TextUtils.isEmpty(item.getContactNumber())) {
                textContact.setText(Html.fromHtml(item.getContactNumber()));
            }
        }
    }

    private void setDefault() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable d = context.getDrawable(android.R.drawable.ic_menu_gallery);
            imgThumbnail.setImageDrawable(d);
        }
        imgThumbnail.setImageDrawable(null);
        textTitle.setText("unknown");
        textAddress.setText("unknown");
        textContact.setText("unknown");
    }
}
