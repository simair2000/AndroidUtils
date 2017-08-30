package com.simair.android.androidutils.example;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.openapi.kakao.data.ImageSearchDocument;
import com.simair.android.androidutils.openapi.kakao.data.ImageSearchResult;
import com.simair.android.androidutils.ui.EndlessRecyclerOnScrollListener;

import java.util.List;

/**
 * Created by simair on 17. 8. 30.
 */

public class ImageSearchFragment extends Fragment {
    private ImageSearchFragmentListener listener;
    private ImageSearchResult result;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
//    private StaggeredGridLayoutManager layoutManager;
    private GridLayoutManager layoutManager;

    public ImageSearchFragment() {
    }

    public void setData(ImageSearchResult result, int page) {
        if(recyclerAdapter != null) {
            if(page == 1) {
                endlessRecyclerOnScrollListener.reset();
                recyclerView.smoothScrollToPosition(0);
                recyclerAdapter.refresh(result);
            } else {
                recyclerAdapter.addResult(result);
            }
        }
    }

    public interface ImageSearchFragmentListener {
        void onLoadMore(ImageSearchFragment fragment, int page);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ImageSearchFragment.ImageSearchFragmentListener) {
            listener = (ImageSearchFragment.ImageSearchFragmentListener)context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public static ImageSearchFragment newInstance(ImageSearchResult result) {
        ImageSearchFragment fragment = new ImageSearchFragment();
        Bundle data = new Bundle();
        data.putSerializable("result", result);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            result = (ImageSearchResult)getArguments().getSerializable("result");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View root) {
        recyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(getContext(), 3);
//        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(listener != null) {
                    listener.onLoadMore(ImageSearchFragment.this, current_page);
                }
            }
        };
        endlessRecyclerOnScrollListener.setVisibleThreshold(15);

        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        private List<ImageSearchDocument> list;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_search_recycler_item, null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
            ImageSearchDocument item = getItem(position);
            holder.setData(item, position);
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public ImageSearchDocument getItem(int position) {
            if(list != null) {
                return list.get(position);
            }
            return null;
        }

        public void refresh(ImageSearchResult result) {
            if(result != null) {
                list = result.getResult();
                notifyDataSetChanged();
            }
        }

        public void addResult(ImageSearchResult result) {
            list.addAll(result.getResult());
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
            }

            public void setData(final ImageSearchDocument item, final int position) {
                ImageView imgThumbnail = (ImageView) view.findViewById(R.id.imgThumbnail);
                Glide.with(getActivity()).load(Uri.parse(item.getThumbnailUrl())).into(imgThumbnail);

                imgThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(ImageGalleryActivity.getIntent(getContext(), list, position));
                    }
                });
            }
        }
    }
}
