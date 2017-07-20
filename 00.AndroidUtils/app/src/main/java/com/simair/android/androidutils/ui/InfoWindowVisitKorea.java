package com.simair.android.androidutils.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.simair.android.androidutils.R;

/**
 * Created by simair on 17. 7. 20.
 */

public class InfoWindowVisitKorea extends LinearLayout {
    private Context context;
    private Button btnPOI;

    public InfoWindowVisitKorea(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.infowindow_visitkorea, this, true);
        initView();
    }

    public InfoWindowVisitKorea(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.infowindow_visitkorea, this, true);
        initView();
    }

    public InfoWindowVisitKorea(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.infowindow_visitkorea, this, true);
        initView();
    }

    private void initView() {
        btnPOI = (Button)findViewById(R.id.btnPOI);
    }

    public void setOnClickListener(OnClickListener l) {
        btnPOI.setOnClickListener(l);
    }
}
