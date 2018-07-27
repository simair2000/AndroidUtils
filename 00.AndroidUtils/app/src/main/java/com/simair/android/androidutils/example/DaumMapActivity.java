package com.simair.android.androidutils.example;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class DaumMapActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener, View.OnClickListener {

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

        initView();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            this.mapType = (MapsActivity.MapType) data.getSerializable("type");
            this.latitude = data.getDouble("latitude", 0);
            this.longitude = data.getDouble("longitude", 0);
        }
    }

    private void initView() {
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);

        findViewById(R.id.btnCurrentLocation).setOnClickListener(this);
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
        }
    }
}
