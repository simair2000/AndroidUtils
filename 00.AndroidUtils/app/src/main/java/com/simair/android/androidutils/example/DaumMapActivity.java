package com.simair.android.androidutils.example;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.kakao.map.APIDaumMap;
import com.simair.android.androidutils.openapi.kakao.CategoryGroup;
import com.simair.android.androidutils.openapi.kakao.CoordType;
import com.simair.android.androidutils.openapi.kakao.Sort;
import com.simair.android.androidutils.openapi.kakao.map.SearchCategoryResult;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;

import java.util.ArrayList;

public class DaumMapActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener, View.OnClickListener, TextView.OnEditorActionListener, Command.CommandListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final String TAG = DaumMapActivity.class.getSimpleName();

    private MapView mapView;
    private MapsActivity.MapType mapType;
    private double latitude;
    private double longitude;
    private boolean askPermissionOnceAgain;
    private DaumMapActivity mActivity;
    private MapPOIItem markerCurrent;
    private MapPOIItem markerTouched;
    private DrawerLayout drawerLayout;
    private Command commandSearchAddress;
    private Command commandRegionCode;
    private Command commandAddress;
    private Command commandConvertCoord;
    private Command commandSearchKeyword;
    private Command commandSearchCategory;
    private ArrayList<MapPOIItem> markersSearchCategory = new ArrayList<>();

    public static Intent getIntent(Context context, MapsActivity.MapType type, double latitude, double longitude) {
        Intent i = new Intent(context, DaumMapActivity.class);
        i.putExtra("type", type);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_daum_map);
        askPermissionOnceAgain = false;
        initCommands();
        initView();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            this.mapType = (MapsActivity.MapType) data.getSerializable("type");
            this.latitude = data.getDouble("latitude", 0);
            this.longitude = data.getDouble("longitude", 0);
        }
    }

    private void initCommands() {
        commandSearchAddress = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                APIDaumMap.getInstance().searchAddress(data.getString("keyword"));
            }
        }.setOnCommandListener(this);

        commandRegionCode = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                APIDaumMap.getInstance().requestRegionCode(data.getDouble("latitude"), data.getDouble("longitude"));
            }
        }.setOnCommandListener(this);

        commandAddress = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                APIDaumMap.getInstance().requestAddress(data.getDouble("latitude"), data.getDouble("longitude"));
            }
        }.setOnCommandListener(this);

        commandConvertCoord = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                String input = data.getString("input");
                String output = data.getString("output");
                String x = data.getString("x");
                String y = data.getString("y");
                APIDaumMap.getInstance().convertCoord(CoordType.valueOf(input), CoordType.valueOf(output), x, y);
            }
        }.setOnCommandListener(this);

        commandSearchKeyword = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                String keyword = data.getString("keyword");
                String category = data.getString("category");
                double latitude = data.getDouble("latitude");
                double longitude = data.getDouble("longitude");
                int radius = data.getInt("radius");
                String sort = data.getString("sort");
                APIDaumMap.getInstance().searchKeyword(keyword, CategoryGroup.valueOf(category), latitude, longitude, radius, Sort.valueOf(sort));
            }
        }.setOnCommandListener(this);

        commandSearchCategory = new Command(){
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                String category = data.getString("category");
                double latitude = data.getDouble("latitude");
                double longitude = data.getDouble("longitude");
                int radius = data.getInt("radius");
                String sort = data.getString("sort");
                SearchCategoryResult result = APIDaumMap.getInstance().searchCategory(CategoryGroup.valueOf(category), latitude, longitude, radius, Sort.valueOf(sort));
                setTag(result);
            }
        }.setOnCommandListener(this);
    }

    private void initView() {
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        ((EditText)findViewById(R.id.editSearch)).setOnEditorActionListener(this);

        findViewById(R.id.btnCurrentLocation).setOnClickListener(this);
        findViewById(R.id.btnMenu).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;
                checkPermissions();
            }
        } else {
            if(latitude == 0 && longitude == 0) {
                setCurrentLocation();
            } else {
                moveLocation(latitude, longitude);
            }
        }
    }

    private MapPOIItem addMarker(String name, int imgResId, MapPoint point) {
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(name);
        marker.setMapPoint(point);
        if(imgResId > 0) {
            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
//            marker.setCustomImageAutoscale(false);
            marker.setCustomImageResourceId(imgResId);
            marker.setCustomImageAnchor(0.5f, 1.0f);
        } else {
            markerCurrent.setMarkerType(MapPOIItem.MarkerType.RedPin);
        }
        mapView.addPOIItem(marker);
        return marker;
    }

    private void moveLocation(double latitude, double longitude) {
        markerCurrent = new MapPOIItem();
        markerCurrent.setItemName("Current Location");
        markerCurrent.setTag(0);
        markerCurrent.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        markerCurrent.setMarkerType(MapPOIItem.MarkerType.BluePin);
        markerCurrent.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(markerCurrent);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
    }

    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            setCurrentLocation();
        }
    }

    private void setCurrentLocation() {
        Utils.getCurrentLocation(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                markerCurrent = new MapPOIItem();
                markerCurrent.setItemName("Current Location");
                markerCurrent.setTag(0);
                markerCurrent.setMapPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()));
                markerCurrent.setMarkerType(MapPOIItem.MarkerType.BluePin);
                markerCurrent.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                mapView.addPOIItem(markerCurrent);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), true);
            }
        });
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DaumMapActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DaumMapActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        Log.d(TAG, "onPOIItemSelected");
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Log.d(TAG, "onCalloutBalloonOfPOIItemTouched - 1");
        MapPoint mapPoint = mapPOIItem.getMapPoint();
        Intent data = new Intent();
        LatLng position = new LatLng(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude);
        data.putExtra("position", position);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Log.d(TAG, "onCalloutBalloonOfPOIItemTouched - 2");
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
        Log.d(TAG, "onDraggablePOIItemMoved");
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        Log.d(TAG, "onMapViewInitialized");
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        Log.d(TAG, "onMapViewCenterPointMoved");
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        Log.d(TAG, "onMapViewZoomLevelChanged");
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        Log.d(TAG, "onMapViewSingleTapped");
        if(markerTouched == null) {
            markerTouched = new MapPOIItem();
            markerTouched.setItemName("Select this location");
            markerTouched.setMarkerType(MapPOIItem.MarkerType.BluePin);
            markerTouched.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            markerTouched.setMapPoint(mapPoint);
            mapView.addPOIItem(markerTouched);
        } else {
            markerTouched.moveWithAnimation(mapPoint, false);
        }
        mapView.setMapCenterPoint(mapPoint, true);

        requestRegionCode(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude);
        requestAddress(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude);
        convertCoord(CoordType.WGS84,
                CoordType.TM,
                String.valueOf(mapPoint.getMapPointGeoCoord().longitude),
                String.valueOf(mapPoint.getMapPointGeoCoord().latitude));
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        Log.d(TAG, "onMapViewDoubleTapped");
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        Log.d(TAG, "onMapViewLongPressed");
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        Log.d(TAG, "onMapViewDragStarted");
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        Log.d(TAG, "onMapViewDragEnded");
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        Log.d(TAG, "onMapViewMoveFinished");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCurrentLocation:
                setCurrentLocation();
                break;
            case R.id.btnMenu:
                toggleDrawerLayout();
                break;
        }
    }

    private void toggleDrawerLayout() {
        if(!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT, true);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT, true);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(!TextUtils.isEmpty(v.getText())) {
            requestSearchAddress(v.getText().toString());
            requestSearchKeyword(v.getText().toString(), CategoryGroup.ALL, latitude, longitude, 1000, Sort.accuracy);
            requestSearchCategory(CategoryGroup.ConvStore, latitude, longitude, 1000, Sort.distance);
            return true;
        }
        return false;
    }

    private void requestSearchCategory(CategoryGroup categoryGroup, double latitude, double longitude, int radius, Sort sort) {
        Bundle data = commandSearchCategory.getData();
        data.putString("category", categoryGroup.name());
        data.putDouble("latitude", latitude);
        data.putDouble("longitude", longitude);
        data.putInt("radius", radius);
        data.putString("sort", sort.name());
        commandSearchCategory.start();
    }

    private void requestSearchKeyword(String keyword, CategoryGroup categoryGroup, double latitude, double longitude, int radius, Sort sort) {
        Bundle data = commandSearchKeyword.getData();
        data.putString("keyword", keyword);
        data.putString("category", categoryGroup.name());
        data.putDouble("latitude", latitude);
        data.putDouble("longitude", longitude);
        data.putInt("radius", radius);
        data.putString("sort", sort.name());
        commandSearchKeyword.start();
    }

    private void requestSearchAddress(String keyword) {
        commandSearchAddress.getData().putString("keyword", keyword);
        commandSearchAddress.start();
    }

    private void requestRegionCode(double latitude, double longitude) {
        Bundle data = commandRegionCode.getData();
        data.putDouble("latitude", latitude);
        data.putDouble("longitude", longitude);
        commandRegionCode.start();
    }

    private void requestAddress(double latitude, double longitude) {
        Bundle data = commandAddress.getData();
        data.putDouble("latitude", latitude);
        data.putDouble("longitude", longitude);
        commandAddress.start();
    }

    private void convertCoord(CoordType input, CoordType output, String x, String y) {
        Bundle data = commandConvertCoord.getData();
        data.putString("input", input.name());
        data.putString("output", output.name());
        data.putString("x", x);
        data.putString("y", y);
        commandConvertCoord.start();
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        if(command == commandSearchCategory) {
            SearchCategoryResult result = (SearchCategoryResult) command.getTag();
            if(result != null && result.documents != null && result.documents.size() > 0) {
                for(SearchCategoryResult.Document document : result.documents) {
                    MapPoint point = MapPoint.mapPointWithGeoCoord(Double.parseDouble(document.y), Double.parseDouble(document.x));
                    MapPOIItem marker = addMarker(document.placeName, R.drawable.pin_pink, point);
                    markersSearchCategory.add(marker);
                }
            }
        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {
        Log.e(TAG, "error[" + errorCode + "] : " + errorMessage);
    }

    @Override
    public void onProgressUpdated(Command command, Bundle data) {

    }
}
