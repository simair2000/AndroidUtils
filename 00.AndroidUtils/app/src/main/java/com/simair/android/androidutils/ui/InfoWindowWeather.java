package com.simair.android.androidutils.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.Marker;
import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.forecast.APIForecast;
import com.simair.android.androidutils.openapi.forecast.CoordinatesConverter;
import com.simair.android.androidutils.openapi.forecast.data.ForecastCurrentObject;
import com.simair.android.androidutils.openapi.forecast.data.WeatherIcon;

import org.json.JSONException;

/**
 * Created by simair on 17. 7. 13.
 */

public class InfoWindowWeather extends LinearLayout implements Command.CommandListener {
    private Context context;
    private double latitude;
    private double longitude;
    private TextView textAddress;
    private ImageView imgIcon;
    private TextView textSky;
    private TextView textDegree;
    private Command commandForecast;
    private Marker marker;
    private ImageView imgWait;

    public InfoWindowWeather(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.infowindow_weather, this, true);
        initView();
    }

    public InfoWindowWeather(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.infowindow_weather, this, true);
        initView();
    }

    public InfoWindowWeather(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.infowindow_weather, this, true);
        initView();
    }

    private void initView() {
        textAddress = (TextView)findViewById(R.id.textAddress);
        imgIcon = (ImageView)findViewById(R.id.imgIcon);
        textSky = (TextView)findViewById(R.id.textSky);
        textDegree = (TextView)findViewById(R.id.textDegree);
        imgWait = (ImageView)findViewById(R.id.imgWait);
        Glide.with(context).load(R.drawable.wait_gif).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imgWait);
    }

    public static View getInstance(Context context) {
        InfoWindowWeather view = new InfoWindowWeather(context);
        return view;
    }

    public void setLocation(Marker marker) {
        this.marker = marker;
        this.latitude = marker.getPosition().latitude;
        this.longitude = marker.getPosition().longitude;
        requestForecast();
    }

    private void requestForecast() {
        commandForecast = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                CoordinatesConverter.Coord coord = CoordinatesConverter.getInstance().geo2coord(latitude, longitude);
                ForecastCurrentObject response = APIForecast.requestCurrent(coord.x, coord.y);
                String address = Utils.getAddress(context, latitude, longitude);
                data.putString("address", address);
                data.putSerializable("forecast", response);
            }
        }.setOnCommandListener(this).start();
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        if(command == commandForecast) {
            String address = data.getString("address");
            ForecastCurrentObject forecast = (ForecastCurrentObject) data.getSerializable("forecast");
            textAddress.setText(address);
            if(forecast != null) {
                textDegree.setText(String.valueOf(forecast.getDegree()) + "°C");

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
                imgWait.setVisibility(GONE);
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }

        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {

    }
}
