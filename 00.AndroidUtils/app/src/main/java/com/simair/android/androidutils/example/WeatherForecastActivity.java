package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.forecast.APIForecast;
import com.simair.android.androidutils.openapi.forecast.CoordinatesConverter;
import com.simair.android.androidutils.openapi.forecast.data.ForecastCurrentObject;
import com.simair.android.androidutils.openapi.forecast.data.WeatherIcon;
import com.simair.android.androidutils.ui.PopupWait;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherForecastActivity extends AppCompatActivity implements Command.CommandListener, View.OnClickListener {

    private static final String TAG = WeatherForecastActivity.class.getSimpleName();
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        textDate.setText(sdf.format(new Date()));

        Utils.getCurrentLocation(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                requestForecast(location);
            }
        });

        findViewById(R.id.btnRefrersh).setOnClickListener(this);
    }

    private void requestForecast(final Location location) {
        commandForecast = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                CoordinatesConverter.Coord coord = CoordinatesConverter.getInstance().geo2coord((float) location.getLatitude(), (float) location.getLongitude());
                ForecastCurrentObject forecast = APIForecast.requestCurrent(coord.x, coord.y);
//                APIForecast.requestTimeData(coord.x, coord.y);
                String address = Utils.getAddress(context, location);
                data.putString("address", address);
                data.putSerializable("forecast", forecast);
            }
        }.setOnCommandListener(this).showWaitDialog(this, PopupWait.getPopupView(this, false)).start();
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

            textAddress.setText(address);
            if(forecast.getSky() == 1) {
                imgIcon.setImageResource(WeatherIcon.SUNNY.iconRes);
                textSky.setText(WeatherIcon.SUNNY.strRes);
            } else if(forecast.getSky() == 2) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_1.iconRes);
                textSky.setText(WeatherIcon.CLOUDY_1.strRes);
            } else if(forecast.getSky() == 3) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_2.iconRes);
                textSky.setText(WeatherIcon.CLOUDY_2.strRes);
            } else if(forecast.getSky() == 4) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_3.iconRes);
                textSky.setText(WeatherIcon.CLOUDY_3.strRes);
            }

            textDegree.setText(String.valueOf(forecast.getDegree()) + "°C");

            // 비
            if(forecast.getPrecipitationType() > 0) {
                // 강수 형태가 있음
                if(forecast.getPrecipitationType() == 1) {
                    // 비
                    imgIcon.setImageResource(WeatherIcon.RAINY.iconRes);
                    textSky.setText(WeatherIcon.RAINY.strRes);
                } else if(forecast.getPrecipitationType() == 2) {
                    // 진눈개비
                    imgIcon.setImageResource(WeatherIcon.RAIN_SNOW.iconRes);
                    textSky.setText(WeatherIcon.RAIN_SNOW.strRes);
                } else if(forecast.getPrecipitationType() == 3) {
                    // 눈
                    imgIcon.setImageResource(WeatherIcon.SNOW.iconRes);
                    textSky.setText(WeatherIcon.SNOW.strRes);
                }
            }

            if(forecast.getThunderbolt() > 0) {
                imgLightning.setVisibility(View.VISIBLE);
            }

            textHumidity.setText(forecast.getHumidity() + "%");
            textRain.setText(forecast.getHourlyPrecipitation() + "mm/h");
        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {
        Log.e(TAG, "errorCode[" + errorCode + "] : " + errorMessage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnMap:
                startActivity(MapsActivity.getIntent(this));
                break;
            case R.id.btnRefrersh:
                Utils.getCurrentLocation(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        requestForecast(location);
                    }
                });
                break;
        }
    }
}
