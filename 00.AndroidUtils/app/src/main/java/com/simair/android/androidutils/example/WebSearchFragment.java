package com.simair.android.androidutils.example;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.openapi.kakao.data.WebSearchDocument;
import com.simair.android.androidutils.openapi.kakao.data.WebSearchResult;
import com.simair.android.androidutils.ui.EndlessScrollListener;

import java.util.List;

/**
 * Created by simair on 17. 8. 29.
 */

public class WebSearchFragment extends Fragment implements AdapterView.OnItemClickListener {
    private WebSearchResult searchResult;
    private ListView listView;
    private ListAdapter listAdapter;
    private EndlessScrollListener endlessScrollListener;
    private WebSearchFragmentListener listener;

    public WebSearchFragment() {
    }

    public interface WebSearchFragmentListener {
        void onLoadMore(WebSearchFragment fragment, int page, int totalItemsCount);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof WebSearchFragmentListener) {
            listener = (WebSearchFragmentListener)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public static WebSearchFragment newInstance(WebSearchResult result) {
        WebSearchFragment fragment = new WebSearchFragment();
        Bundle data = new Bundle();
        data.putSerializable("result", result);
        fragment.setArguments(data);
        return fragment;
    }

    public void setData(WebSearchResult data, int page) {
        searchResult = data;
        if(listAdapter != null) {
            if(page == 1) {
                listAdapter.refresh(searchResult);
            } else {
                listAdapter.addResult(data);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            searchResult = (WebSearchResult) getArguments().getSerializable("result");
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

        listAdapter.refresh(searchResult);

        listView.setOnItemClickListener(this);

        endlessScrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if(listener != null) {
                    listener.onLoadMore(WebSearchFragment.this, page, totalItemsCount);
                }
                return true;
            }
        };
        listView.setOnScrollListener(endlessScrollListener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WebSearchDocument item = listAdapter.getItem(position);
        String url = item.getUrl();
        Utils.startWebBrowser(getContext(), url);
    }

    private class ListAdapter extends BaseAdapter {

        private final LayoutInflater inflater;
        private List<WebSearchDocument> list;

        public ListAdapter() {
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public WebSearchDocument getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WebSearchDocument item = getItem(position);
            View view = inflater.inflate(R.layout.web_search_list_item, null);
            ((TextView)view.findViewById(R.id.textTitle)).setText(Html.fromHtml(item.getTitle()));
            ((TextView)view.findViewById(R.id.textContent)).setText(Html.fromHtml(item.getContents()));
            ((TextView)view.findViewById(R.id.textDateTime)).setText(item.getDateTime());
            return view;
        }

        public void refresh(WebSearchResult searchResult) {
            if(searchResult != null) {
                list = searchResult.getResult();
                notifyDataSetChanged();
            }
        }

        public void addResult(WebSearchResult data) {
            list.addAll(data.getResult());
            notifyDataSetChanged();
        }
    }
}
