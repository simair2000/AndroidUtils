package com.simair.android.androidutils.ui;

import android.view.View;

/**
 * Created by simair on 2016-03-29.
 */
public class ViewChanger {
    private View[] views;

    public ViewChanger(View... v) {
        this.views = v;
        setCurrentView(0);
    }

    public View getView(int index) {
        if(views != null && views.length > 0) {
            return views[index];
        }
        return null;
    }

    public void setCurrentView(int index) {
        View v = getView(index);
        if(v != null) {
            setCurrentView(v);
        }
    }

    public void setCurrentView(View v) {
        if (views != null) {
            for (View view : views) {
                if (view.equals(v)) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        } else {
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        }
    }

    public View getCurrentView() {
        if(views != null) {
            for(View view : views) {
                if(view.getVisibility() == View.VISIBLE) {
                    return view;
                }
            }
        }
        return null;
    }
}
