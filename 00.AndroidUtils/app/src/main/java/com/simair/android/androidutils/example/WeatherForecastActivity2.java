package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.simair.android.androidutils.openapi.airpollution.data.AirPollutionIcon;
import com.simair.android.androidutils.openapi.airpollution.data.AirPollutionObject;
import com.simair.android.androidutils.openapi.airpollution.data.AirPollutionParam;
import com.simair.android.androidutils.openapi.airpollution.data.DustObject;
import com.simair.android.androidutils.openapi.forecast.CoordinatesConverter;
import com.simair.android.androidutils.openapi.forecast.FacadeForecastCurrent;
import com.simair.android.androidutils.openapi.forecast.FacadeForecastTime;
import com.simair.android.androidutils.openapi.forecast.data.ForecastCurrentObject;
import com.simair.android.androidutils.openapi.forecast.data.ForecastTimeObject;
import com.simair.android.androidutils.openapi.forecast.data.WeatherIcon;
import com.simair.android.androidutils.ui.BitmapUtil;
import com.simair.android.androidutils.ui.WeatherListItem;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

public class WeatherForecastActivity2 extends AppCompatActivity implements Command.CommandListener, View.OnClickListener {

    private static final int REQ_LOCATION = 100;
    private RecyclerView recyclerView;
    private WeatherRecyclerAdapter recyclerAdapter;
    private Command commandForecast = new Command(){
        @Override
        public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
            float latitude = data.getFloat("latitude");
            float longitude = data.getFloat("longitude");
            CoordinatesConverter.Coord coord = CoordinatesConverter.getInstance().geo2coord(latitude, longitude);
            ForecastCurrentObject forecast = FacadeForecastCurrent.getInstance(context).get(coord);
            String address = Utils.getAddress(context, latitude, longitude);
            data.putString("address", address);
            data.putSerializable("forecast", forecast);

            HashMap<String, ForecastTimeObject> timeData = FacadeForecastTime.getInstance(context).get(coord);
            data.putSerializable("timeData", timeData);
        }
    };
    private Command commandAirpollution = new Command() {
        @Override
        public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
            float latitude = data.getFloat("latitude");
            float longitude = data.getFloat("longitude");
            AirPollutionParam param = new AirPollutionParam();
            param.setLatitude(latitude);
            param.setLongitude(longitude);
            Address addr = Utils.getAddressClass(context, latitude, longitude);
            param.setAdminName(addr.getAdminArea());
            param.setLocalityName(addr.getLocality());
            param.setThoroughFare(addr.getThoroughfare());
            AirPollutionObject airPollution = FacadeAirPollution.getInstance(context).get(param);
            data.putSerializable("airPollution", airPollution);
        }
    };
    private Context context;
    private TextView textAddress;
    private ImageView imgIcon;
    private TextView textWeather;
    private TextView textDegree;
    private TextView textHumidity;
    private TextView textRain;
    private TextView textPollutionGrade;
    private TextView textPm10;
    private TextView textPm25;
    private ImageView imgAirPollution;
    private double latitude;
    private double longitude;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, WeatherForecastActivity2.class);
        return i;
    }

    public static Intent getIntent(Context context, double latitude, double longitude) {
        Intent i = new Intent(context, WeatherForecastActivity2.class);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_weather_forecast2);
        initView();
        Bundle data = getIntent().getExtras();
        if(data != null) {
            latitude = data.getDouble("latitude", 0);
            longitude = data.getDouble("longitude", 0);
            if(latitude != 0 || longitude != 0) {
                requestForecast(latitude, longitude);
                requestAirpollution(latitude, longitude);
                return;
            }
        }
        Utils.getCurrentLocation(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                requestForecast(location.getLatitude(), location.getLongitude());
                requestAirpollution(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void requestForecast(double latitude, double longitude) {
        Bundle data = commandForecast.getData();
        data.putFloat("latitude", (float) latitude);
        data.putFloat("longitude", (float) longitude);
        commandForecast.setData(data).setOnCommandListener(this).showWaitDialog(this).start();
    }

    private void requestAirpollution(double latitude, double longitude) {
        Bundle data = commandAirpollution.getData();
        data.putFloat("latitude", (float) latitude);
        data.putFloat("longitude", (float) longitude);
        commandAirpollution.setData(data).setOnCommandListener(this).start();
    }

    private void initView() {
        ImageView imgBG = findViewById(R.id.imgBG);
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(sdf.format(new Date()));
        if(6 < hour && hour <= 18) {
            imgBG.setImageResource(R.drawable.bg_day);
        } else {
            imgBG.setImageResource(R.drawable.bg_night);
        }
        BitmapUtil.makeBlur(this, imgBG, 1);

        textAddress = (TextView)findViewById(R.id.textAddress);
        imgIcon = (ImageView)findViewById(R.id.imgIcon);
        textWeather = (TextView)findViewById(R.id.textWeather);
        textDegree = (TextView)findViewById(R.id.textDegree);
        textHumidity = (TextView)findViewById(R.id.textHumidity);
        textRain = (TextView)findViewById(R.id.textRain);

        imgAirPollution = (ImageView)findViewById(R.id.imgAirPollution);
        textPollutionGrade = (TextView)findViewById(R.id.textPollutionGrade);
        textPm10 = (TextView)findViewById(R.id.textPm10);
        textPm25 = (TextView)findViewById(R.id.textPm25);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerAdapter = new WeatherForecastActivity2.WeatherRecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);

        findViewById(R.id.btnMap).setOnClickListener(this);
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
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                Date date = sdf.parse(forecast.getBaseDate() + forecast.getBaseTime());
                sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                textAddress.setText(address + "\n측정일시(" + sdf.format(date) + ")");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            textDegree.setText(String.valueOf(forecast.getDegree() == 0 ? forecast.getDegree3() : forecast.getDegree()));
            textHumidity.setText(forecast.getHumidity() + "");
            textRain.setText(forecast.getHourlyPrecipitation() + "");

            if(forecast.getSky() == 1) {
                imgIcon.setImageResource(WeatherIcon.SUNNY.getTimedIcon(-1));
                textWeather.setText(WeatherIcon.SUNNY.strRes);
            } else if(forecast.getSky() == 2) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_1.getTimedIcon(-1));
                textWeather.setText(WeatherIcon.CLOUDY_1.strRes);
            } else if(forecast.getSky() == 3) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_2.getTimedIcon(-1));
                textWeather.setText(WeatherIcon.CLOUDY_2.strRes);
            } else if(forecast.getSky() == 4) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_3.getTimedIcon(-1));
                textWeather.setText(WeatherIcon.CLOUDY_3.strRes);
            }

            if(forecast.getPrecipitationType() > 0) {
                // 강수 형태가 있음
                WeatherIcon icon = WeatherIcon.CLOUDY_3;
                if(forecast.getPrecipitationType() == 1) {
                    // 비
                    icon = WeatherIcon.getRainIcon(forecast.getHourlyPrecipitation());
                } else if(forecast.getPrecipitationType() == 2) {
                    // 진눈개비
                    icon = WeatherIcon.getRainSnowIcon(forecast.getHourlyPrecipitation());
                } else if(forecast.getPrecipitationType() == 3) {
                    // 눈
                    icon = WeatherIcon.getSnowIcon(forecast.getHourlyPrecipitation());
                }

                if(forecast.getThunderbolt() > 0) {
                    imgIcon.setImageResource(WeatherIcon.RAIN_LIGHTNING.iconRes);
                } else {
                    imgIcon.setImageResource(icon.getTimedIcon(-1));
                }
                textWeather.setText(icon.strRes);

            } else {
                if(forecast.getThunderbolt() > 0) {
                    imgIcon.setImageResource(WeatherIcon.LIGHTNING.getTimedIcon(-1));
                }
            }

            HashMap<String, ForecastTimeObject> timeData = (HashMap<String, ForecastTimeObject>) data.getSerializable("timeData");
            if(timeData != null) {
                recyclerAdapter.setData(timeData);
            }

        } else if(command == commandAirpollution) {
            AirPollutionObject airPollution = (AirPollutionObject) data.getSerializable("airPollution");
            if(airPollution != null) {
                String pm10Value = airPollution.getPm10Value();
                String pm25Value = airPollution.getPm25Value();
                AirPollutionIcon item = AirPollutionIcon.getIcon(pm10Value);
                imgAirPollution.setImageResource(item.resId);
                textPollutionGrade.setText(item.label);
                textPm10.setText(pm10Value);
                textPm25.setText(pm25Value);
            }
        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {

    }

    @Override
    public void onProgressUpdated(Command command, Bundle data) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMap:
                startActivityForResult(DaumMapActivity.getIntent(this, MapsActivity.MapType.TYPE_WEATHER, latitude, longitude), REQ_LOCATION);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_LOCATION) {
            if(resultCode == RESULT_OK) {
                LatLng position = data.getParcelableExtra("position");
                latitude = position.latitude;
                longitude = position.longitude;
                requestForecast(position.latitude, position.longitude);
                requestAirpollution(position.latitude, position.longitude);
            }
        }
    }

    private class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherForecastActivity2.WeatherRecyclerAdapter.ViewHolder> {

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
        public WeatherForecastActivity2.WeatherRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            WeatherListItem view = new WeatherListItem(parent.getContext());
            WeatherForecastActivity2.WeatherRecyclerAdapter.ViewHolder holder = new WeatherForecastActivity2.WeatherRecyclerAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(WeatherForecastActivity2.WeatherRecyclerAdapter.ViewHolder holder, int position) {
            ForecastTimeObject item = getItem(position);
            holder.setData(item);
        }

        @Override
        public int getItemCount() {
            return dataMap == null ? 0 : dataMap.size();
        }
    }
}
