package com.simair.android.androidutils.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by simair on 17. 7. 18.
 */

public class BaseActivity extends AppCompatActivity {

    protected Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.context = this;
        super.onCreate(savedInstanceState);
    }
}
