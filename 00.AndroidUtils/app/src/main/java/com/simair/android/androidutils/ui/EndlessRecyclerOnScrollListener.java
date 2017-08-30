package com.simair.android.androidutils.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by simair on 17. 7. 10.
 * Referenced at <br />
 * @see <a href="https://gist.github.com/ssinss/e06f12ef66c51252563e">https://gist.github.com/ssinss/e06f12ef66c51252563e</a>
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private RecyclerView.LayoutManager layoutManager;

    boolean loadingTop;

    public EndlessRecyclerOnScrollListener(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public void setVisibleThreshold(int count) {
        visibleThreshold = count < 5 ? 5 : count;
    }

    public void reset() {
        previousTotal = 0;
        loading = true;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        current_page = 1;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        if(layoutManager instanceof StaggeredGridLayoutManager) {
            firstVisibleItem = ((StaggeredGridLayoutManager)layoutManager).findFirstVisibleItemPositions(null)[0];
        } else if(layoutManager instanceof LinearLayoutManager) {
            firstVisibleItem = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
        } else if(layoutManager instanceof GridLayoutManager) {
            firstVisibleItem = ((GridLayoutManager)layoutManager).findFirstVisibleItemPosition();
        }

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if(!loading) {
            if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                // Do something
                current_page++;

                onLoadMore(current_page);

                loading = true;
            }
        }

        if(firstVisibleItem == 0 && dy < 0 && !loadingTop) {
            onLoadTop();
            loadingTop = true;
        }
    }

    public void refreshTopComplete() {
        loadingTop = false;
    }

    public abstract void onLoadMore(int current_page);

    public void onLoadTop() {}
}
