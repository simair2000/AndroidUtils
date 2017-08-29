package com.simair.android.androidutils.example;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.openapi.kakao.data.VideoSearchDocument;
import com.simair.android.androidutils.openapi.kakao.data.VideoSearchResult;
import com.simair.android.androidutils.ui.EndlessScrollListener;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by simair on 17. 8. 29.
 */

public class VideoSearchFragment extends Fragment implements AdapterView.OnItemClickListener {
    private VideoSearchFragmentListener listener;
    private VideoSearchResult result;
    private ListView listView;
    private ListAdapter listAdapter;
    private EndlessScrollListener listScroller;

    public VideoSearchFragment() {
    }

    public void setData(VideoSearchResult result, int page) {
        if(listAdapter != null) {
            if(page == 1) {
                listScroller.resetPage();
                listView.smoothScrollToPosition(0);
                listAdapter.refresh(result);
            } else {
                listAdapter.addResult(result);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideoSearchDocument item = listAdapter.getItem(position);
        String url = item.getUrl();
        Utils.startWebBrowser(getContext(), url);
    }

    public interface VideoSearchFragmentListener {
        void onLoadMore(VideoSearchFragment fragment, int page, int total);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof VideoSearchFragmentListener) {
            listener = (VideoSearchFragmentListener)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public static VideoSearchFragment newInstance(VideoSearchResult result) {
        VideoSearchFragment fragment = new VideoSearchFragment();
        Bundle data = new Bundle();
        data.putSerializable("result", result);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            result = (VideoSearchResult)getArguments().getSerializable("result");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_search_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View root) {
        listView = (ListView)root.findViewById(R.id.listView);
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);
        listAdapter.refresh(result);

        listView.setOnItemClickListener(this);

        listScroller = new EndlessScrollListener() {

            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if(listener != null) {
                    listener.onLoadMore(VideoSearchFragment.this, page, totalItemsCount);
                }
                return true;
            }
        };
        listView.setOnScrollListener(listScroller);
    }

    private String convertMillisecondsToFormat(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private class ListAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private List<VideoSearchDocument> list;

        public ListAdapter() {
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public VideoSearchDocument getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            VideoSearchDocument item = getItem(position);
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.video_search_list_item, null);
            }
            ImageView imgThumbnail = (ImageView) convertView.findViewById(R.id.imgThumbnail);
            Glide.with(getActivity()).load(Uri.parse(item.getThumbnailUrl())).into(imgThumbnail);
            ((TextView)convertView.findViewById(R.id.textTitle)).setText(Html.fromHtml(item.getTitle()));
            ((TextView)convertView.findViewById(R.id.textDateTime)).setText(Html.fromHtml(item.getDateTime()));
            ((TextView)convertView.findViewById(R.id.textAuthor)).setText(Html.fromHtml(item.getAuthor()));
            ((TextView)convertView.findViewById(R.id.textPlaytime)).setText(convertMillisecondsToFormat(item.getPlaySeconds() * 1000));
            return convertView;
        }

        public void refresh(VideoSearchResult data) {
            if(data != null) {
                list = data.getResult();
                notifyDataSetChanged();
            }
        }

        public void addResult(VideoSearchResult data) {
            list.addAll(data.getResult());
            notifyDataSetChanged();
        }
    }
}
