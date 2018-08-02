package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.airpollution.FacadeAirPollution;
import com.simair.android.androidutils.openapi.airpollution.data.AirPollutionParam;
import com.simair.android.androidutils.openapi.airpollution.data.DustObject;
import com.simair.android.androidutils.openapi.forecast.APIForecast;
import com.simair.android.androidutils.openapi.forecast.CoordinatesConverter;
import com.simair.android.androidutils.openapi.forecast.FacadeForecastCurrent;
import com.simair.android.androidutils.openapi.forecast.FacadeForecastTime;
import com.simair.android.androidutils.openapi.forecast.data.ForecastCurrentObject;
import com.simair.android.androidutils.openapi.forecast.data.ForecastTimeObject;
import com.simair.android.androidutils.openapi.forecast.data.WeatherIcon;
import com.simair.android.androidutils.ui.PopupWait;
import com.simair.android.androidutils.ui.WeatherListItem;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * 동네 예보 연동 테스트 코드
 */
public class WeatherForecastActivity extends AppCompatActivity implements Command.CommandListener, View.OnClickListener {

    private static final String TAG = WeatherForecastActivity.class.getSimpleName();
    private static final int REQ_LOCATION = 100;
    private Command commandTest;
    private Command commandForecast;
    private Context context;
    private TextView textDate;
    private TextView textAddress;
    private ImageView imgIcon;
    private TextView textSky;
    private TextView textDegree;
    private View imgLightning;
    private TextView textHumidity;
    private TextView textRain;
    private double latitude;
    private double longitude;
    private RecyclerView recyclerView;
    private WeatherRecyclerAdapter recyclerAdapter;
    private ImageView imgDustIcon;
    private TextView textDust;
    private TextView textDustValue;
    private ImageView imgBg;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, WeatherForecastActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_weather_forecast);
        initView();
    }

    private void initView() {
        findViewById(R.id.btnMap).setOnClickListener(this);

//        CoordinatesConverter.Coord coord = CoordinatesConverter.getInstance().geo2coord((float) 37.405148, (float) 127.097248);
//        Log.e("simair", "geo2coord [37.405148, 127.097248] : " + coord.x + ", " + coord.y);
//        CoordinatesConverter.GeoCode geo = CoordinatesConverter.getInstance().coord2geo((float) 60.905716, (float) 122.235306);
//        Log.e("simair", "coord2geo [60.905716, 122.235306] : " + geo.latitude + ", " + geo.longitude);

//        requestTest();

        textDate = (TextView)findViewById(R.id.textDate);
        textAddress = (TextView)findViewById(R.id.textAddress);
        imgIcon = (ImageView)findViewById(R.id.imgIcon);
        textSky = (TextView)findViewById(R.id.textSky);
        textDegree = (TextView)findViewById(R.id.textDegree);
        imgLightning = findViewById(R.id.imgLightning);
        textHumidity = (TextView)findViewById(R.id.textHumidity);
        textRain = (TextView)findViewById(R.id.textRain);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerAdapter = new WeatherRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
//        textDate.setText(sdf.format(new Date()));

        Utils.getCurrentLocation(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                requestForecast(location.getLatitude(), location.getLongitude());
            }
        });

        findViewById(R.id.btnRefrersh).setOnClickListener(this);

        imgDustIcon = (ImageView)findViewById(R.id.imgDustIcon);
        textDust = (TextView)findViewById(R.id.textDust);
        textDustValue = (TextView)findViewById(R.id.textDustValue);

        imgBg = (ImageView)findViewById(R.id.imgBg);
    }

    private void requestForecast(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        commandForecast = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {

                CoordinatesConverter.Coord coord = CoordinatesConverter.getInstance().geo2coord((float) latitude, (float) longitude);
                ForecastCurrentObject forecast = FacadeForecastCurrent.getInstance(context).get(coord);
                String address = Utils.getAddress(context, latitude, longitude);
                data.putString("address", address);
                data.putSerializable("forecast", forecast);

                HashMap<String, ForecastTimeObject> timeData = FacadeForecastTime.getInstance(context).get(coord);
                data.putSerializable("timeData", timeData);

                /*AirPollutionParam param = new AirPollutionParam();
                param.setLatitude(latitude);
                param.setLongitude(longitude);
                Address addr = Utils.getAddressClass(context, latitude, longitude);
                param.setAdminName(addr.getAdminArea());
                param.setLocalityName(addr.getLocality());
                param.setThoroughFare(addr.getThoroughfare());
                ArrayList<DustObject> dustList = FacadeAirPollution.getInstance(context).get(param);
                data.putSerializable("dustList", dustList);*/

            }
        }.setOnCommandListener(this).showWaitDialog(this, PopupWait.getPopupView(this, true)).start();
    }

    private void requestTest() {
        commandTest = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                APIForecast.requestCurrent((float) 60.905716, (float) 122.235306);
                APIForecast.requestTimeData((float) 60.905716, (float) 122.235306);
            }
        }.setOnCommandListener(this).showWaitDialog(this, PopupWait.getPopupView(this, false)).start();
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        if(command == commandForecast) {
            String address = data.getString("address");
            ForecastCurrentObject forecast = (ForecastCurrentObject) data.getSerializable("forecast");
            if(forecast == null) {
                Toast.makeText(context, "날씨 정보 가져오기 실패!!", Toast.LENGTH_SHORT).show();
                return;
            }

            Glide.with(this).load(R.drawable.sunny).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgBg);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                Date date = sdf.parse(forecast.getBaseDate() + forecast.getBaseTime());
                sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                textDate.setText("측정일시 : " + sdf.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            textAddress.setText(address);
            if(forecast.getSky() == 1) {
                imgIcon.setImageResource(WeatherIcon.SUNNY.iconRes);
                textSky.setText(WeatherIcon.SUNNY.strRes);
                Glide.with(this).load(R.drawable.sunny).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgBg);
            } else if(forecast.getSky() == 2) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_1.iconRes);
                textSky.setText(WeatherIcon.CLOUDY_1.strRes);
                Glide.with(this).load(R.drawable.cloudy1).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgBg);
            } else if(forecast.getSky() == 3) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_2.iconRes);
                textSky.setText(WeatherIcon.CLOUDY_2.strRes);
                Glide.with(this).load(R.drawable.cloudy1).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgBg);
            } else if(forecast.getSky() == 4) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_3.iconRes);
                textSky.setText(WeatherIcon.CLOUDY_3.strRes);
                Glide.with(this).load(R.drawable.cloudy1).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgBg);
            }

            textDegree.setText(String.valueOf(forecast.getDegree() == 0 ? forecast.getDegree3() : forecast.getDegree()) + "°C");


            // 비
            if(forecast.getPrecipitationType() > 0) {
                // 강수 형태가 있음
                WeatherIcon icon = WeatherIcon.CLOUDY_3;
                if(forecast.getPrecipitationType() == 1) {
                    // 비
                    icon = WeatherIcon.getRainIcon(forecast.getHourlyPrecipitation());
                    Glide.with(this).load(R.drawable.rain1).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgBg);
                } else if(forecast.getPrecipitationType() == 2) {
                    // 진눈개비
                    icon = WeatherIcon.getRainSnowIcon(forecast.getHourlyPrecipitation());
                    Glide.with(this).load(R.drawable.rain2).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgBg);
                } else if(forecast.getPrecipitationType() == 3) {
                    // 눈
                    icon = WeatherIcon.getSnowIcon(forecast.getHourlyPrecipitation());
                    Glide.with(this).load(R.drawable.snow).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgBg);
                }

                if(forecast.getThunderbolt() > 0) {
                    imgIcon.setImageResource(WeatherIcon.RAIN_LIGHTNING.iconRes);
                } else {
                    imgIcon.setImageResource(icon.iconRes);
                }
                textSky.setText(icon.strRes);

            } else {
                if(forecast.getThunderbolt() > 0) {
                    imgIcon.setImageResource(WeatherIcon.CLOUDY_LIGHTNING.iconRes);
                }
            }

            textHumidity.setText(forecast.getHumidity() + "%");
            textRain.setText(forecast.getHourlyPrecipitation() + "mm/h");

            HashMap<String, ForecastTimeObject> timeData = (HashMap<String, ForecastTimeObject>) data.getSerializable("timeData");
            if(timeData != null) {
                recyclerAdapter.setData(timeData);
            }

            ArrayList<DustObject> dustList = (ArrayList<DustObject>) data.getSerializable("dustList");
            if(dustList != null) {
                DustObject dustData = dustList.get(0);
                String grade = dustData.getPm10().getGrade();
                float dustValue = dustData.getPm10().getValue();
                textDust.setText(grade);
                textDustValue.setText(dustValue + "㎍/㎥");
                if(dustValue <= 30) {
                    // 좋음
                    imgDustIcon.setImageResource(R.drawable.icon_good);
                } else if(30 < dustValue && dustValue <= 80) {
                    // 보통
                    imgDustIcon.setImageResource(R.drawable.icon_normal);
                } else if(80 < dustValue && dustValue <= 120) {
                    // 약간 나쁨
                    imgDustIcon.setImageResource(R.drawable.icon_bad_1);
                } else if(120 < dustValue && dustValue <= 200) {
                    // 나쁨
                    imgDustIcon.setImageResource(R.drawable.icon_bad_2);
                } else {
                    // 매우 나쁨
                    imgDustIcon.setImageResource(R.drawable.icon_bad_3);
                }
            }
        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {
        Log.e(TAG, "errorCode[" + errorCode + "] : " + errorMessage);
    }

    @Override
    public void onProgressUpdated(Command command, Bundle data) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnMap:
//                startActivityForResult(MapsActivity.getIntent(context, MapsActivity.MapType.TYPE_WEATHER, latitude, longitude), REQ_LOCATION);
                startActivityForResult(DaumMapActivity.getIntent(context, MapsActivity.MapType.TYPE_WEATHER, latitude, longitude), REQ_LOCATION);
                break;
            case R.id.btnRefrersh:
                requestForecast(latitude, longitude);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_LOCATION) {
            if(resultCode == RESULT_OK) {
                LatLng latLng = data.getParcelableExtra("position");
                requestForecast(latLng.latitude, latLng.longitude);
            }
        }
    }

    private class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.ViewHolder> {

        private TreeMap<String, ForecastTimeObject> dataMap;

        public void setData(HashMap<String, ForecastTimeObject> timeData) {
            dataMap = new TreeMap<>(timeData);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final WeatherListItem view;

            public ViewHolder(WeatherListItem itemView) {
                super(itemView);
                this.view = itemView;
            }

            public void setData(ForecastTimeObject data) {
                view.setData(data);
            }
        }

        public ForecastTimeObject getItem(int position) {
            Iterator<String> itr = dataMap.keySet().iterator();
            int i = 0;
            while (itr.hasNext()) {
                String key = itr.next();
                if(i == position) {
                    return dataMap.get(key);
                }
                i++;
            }
            return null;
        }

        @Override
        public WeatherRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            WeatherListItem view = new WeatherListItem(parent.getContext());
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(WeatherRecyclerAdapter.ViewHolder holder, int position) {
            ForecastTimeObject item = getItem(position);
            holder.setData(item);
        }

        @Override
        public int getItemCount() {
            return dataMap == null ? 0 : dataMap.size();
        }
    }
}
