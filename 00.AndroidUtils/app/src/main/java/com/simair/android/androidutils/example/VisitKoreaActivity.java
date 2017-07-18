package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.visitkorea.APIVisitKorea;
import com.simair.android.androidutils.openapi.visitkorea.TourType;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaLocationBasedListObject;
import com.simair.android.androidutils.ui.BaseActivity;
import com.simair.android.androidutils.ui.EndlessScrollListener;
import com.simair.android.androidutils.ui.PopupWait;
import com.simair.android.androidutils.ui.TourGuideListItem;

import org.json.JSONException;

import java.util.ArrayList;


public class VisitKoreaActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, OnSuccessListener<Location>,Command.CommandListener, AdapterView.OnItemClickListener {

    private static final int MAX_ITEM_COUNT = 10;
    private Spinner spinner;
    private LayoutInflater inflater;
    private SpinnerAdapter spinnerAdapter;
    private Spinner spinnerRange;
    private int range;  // 검색 반경 [m 미터]
    private TextView textAddress;
    private double latitude;
    private double longitude;
    private Command commandList;
    private int currentPage = 1;
    private ListView listView;
    private ListAdapter listAdapter;
    private EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public boolean onLoadMore(int page, int totalItemsCount) {
            requestList(++currentPage, false);
            return true;
        }
    };

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, VisitKoreaActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        setContentView(R.layout.activity_visit_korea);
        initProperties();
        initView();
        Utils.getCurrentLocation(this, this);
    }

    private void initProperties() {
        currentPage = 1;
        endlessScrollListener.resetPage();
    }

    private void initView() {
        textAddress = (TextView)findViewById(R.id.textAddress);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinnerAdapter = new SpinnerAdapter();
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        spinnerRange = (Spinner)findViewById(R.id.spinnerRange);
        spinnerRange.setSelection(4);

        spinner.setEnabled(false);
        spinnerRange.setEnabled(false);

        listView = (ListView)findViewById(R.id.listView);
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnScrollListener(endlessScrollListener);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.equals(spinner)) {
            TourType item = spinnerAdapter.getItem(position);
            spinnerRange.setOnItemSelectedListener(this);
//            Toast.makeText(this, "item : " + item.name(), Toast.LENGTH_SHORT).show();
        } else if(parent.equals(spinnerRange)) {
            String[] ranges = getResources().getStringArray(R.array.visitkorea_range_list);
//            Toast.makeText(this, "range : " + ranges[position], Toast.LENGTH_SHORT).show();
            if(position == 0) {
                range = 100;
            } else if(position == 1) {
                range = 200;
            } else if(position == 2) {
                range = 500;
            } else if(position == 3) {
                range = 1000;
            } else if(position == 4) {
                range = 2000;
            } else if(position == 5) {
                range = 5000;
            } else if(position == 6) {
                range = 10000;
            } else if(position == 7) {
                range = 15000;
            } else {
                range = 20000;
            }
        }
        endlessScrollListener.resetPage();
        requestList(1, true);
    }

    private void requestList(int page, boolean showWait) {
        currentPage = page;
        commandList = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                TourType type = (TourType) spinner.getSelectedItem();
                ArrayList<VisitKoreaLocationBasedListObject> list = APIVisitKorea.getInstance().requestLocationBasedInfo(MAX_ITEM_COUNT, currentPage, type, longitude, latitude, range);
                data.putSerializable("list", list);
            }
        }.setOnCommandListener(this);
        if(showWait) {
            commandList.showWaitDialog(this, PopupWait.getPopupView(this, true));
        }
        commandList.start();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSuccess(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        spinner.setEnabled(true);
        spinnerRange.setEnabled(true);
        String address = Utils.getAddress(this, location.getLatitude(), location.getLongitude());
        textAddress.setText(address);
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        if(command == commandList) {
            ArrayList<VisitKoreaLocationBasedListObject> list = (ArrayList<VisitKoreaLocationBasedListObject>) data.getSerializable("list");
            listAdapter.refresh(list);
        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {
        if(command == commandList) {
            listAdapter.refresh(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VisitKoreaLocationBasedListObject item = listAdapter.getItem(position);
        startActivity(TourGuideDetailActivity.getIntent(this, item));
    }

    private class SpinnerAdapter extends BaseAdapter {

        private final TourType[] list;

        public SpinnerAdapter() {
            list = TourType.values();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.length;
        }

        @Override
        public TourType getItem(int i) {
            return TourType.values()[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = inflater.inflate(R.layout.tour_type_spinner_item, null);
            }
            TourType type = getItem(i);
            ((TextView)view.findViewById(R.id.textType)).setText(type.stringRes);
            return view;
        }
    }

    private class ListAdapter extends BaseAdapter {

        private ArrayList<VisitKoreaLocationBasedListObject> list;

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public VisitKoreaLocationBasedListObject getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TourGuideListItem v = (TourGuideListItem) view;
            if(v == null) {
                v = new TourGuideListItem(context);
            }
            v.setItem(getItem(i));
            return v;
        }

        public void refresh(ArrayList<VisitKoreaLocationBasedListObject> resultList) {
            if(currentPage == 1 || list == null) {
                list = resultList;
                listView.smoothScrollToPosition(0);
            } else {
                if(resultList != null) {
                    list.addAll(resultList);
                }
            }
            notifyDataSetChanged();
        }
    }
}
