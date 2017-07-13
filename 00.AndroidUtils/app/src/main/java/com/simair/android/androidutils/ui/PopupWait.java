package com.simair.android.androidutils.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.simair.android.androidutils.R;

/**
 * Created by simair on 17. 7. 12.
 */

public class PopupWait extends LinearLayout {

    private ImageView imgView;

    public PopupWait(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.popup_wait, this, true);
        initView();
    }

    public PopupWait(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.popup_wait, this, true);
        initView();
    }

    public PopupWait(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.popup_wait, this, true);
        initView();
    }

    private void initView() {
        imgView = (ImageView)findViewById(R.id.imageView);
        Glide.with(getContext()).load(R.drawable.wait_gif2).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgView);

//        ObjectAnimator anim = ObjectAnimator.ofFloat(imgView, "rotation", 360);
//        anim.setDuration(800);
//        anim.setRepeatMode(ValueAnimator.RESTART);
//        anim.setRepeatCount(ValueAnimator.INFINITE);
//        anim.setInterpolator(null);
//        anim.start();
    }

    public static CustomPopup getPopupView(Context context, boolean isModal) {
        PopupWait view = new PopupWait(context);
        CustomPopup popup = new CustomPopup(context, !isModal).build(view, null, null).setCancelable(!isModal).setCanceledOnTouchOutside(!isModal);
        return popup;
    }
}
