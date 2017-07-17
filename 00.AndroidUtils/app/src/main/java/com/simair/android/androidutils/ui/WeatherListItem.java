package com.simair.android.androidutils.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simair.android.androidutils.R;
import com.simair.android.androidutils.openapi.forecast.data.ForecastTimeObject;
import com.simair.android.androidutils.openapi.forecast.data.WeatherIcon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by simair on 17. 7. 17.
 */

public class WeatherListItem extends LinearLayout {
    private Context context;
    private ImageView imgIcon;
    private TextView textSky;
    private TextView textDegree;
    private TextView textHumidity;
    private TextView textRain;
    private TextView textDate;

    public WeatherListItem(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.weather_list_item, this, true);
        initView();
    }

    public WeatherListItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.weather_list_item, this, true);
        initView();
    }

    public WeatherListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.weather_list_item, this, true);
        initView();
    }

    private void initView() {
        textDate = (TextView)findViewById(R.id.textDate);
        imgIcon = (ImageView)findViewById(R.id.imgIcon);
        textSky = (TextView)findViewById(R.id.textSky);
        textDegree = (TextView)findViewById(R.id.textDegree);
        textHumidity = (TextView)findViewById(R.id.textHumidity);
        textRain = (TextView)findViewById(R.id.textRain);
    }

    public void setData(ForecastTimeObject data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
            Date date = sdf.parse(data.getForecastTime());
            sdf = new SimpleDateFormat("HH:mm");
            textDate.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(data.getData().getSky() == 1) {
            imgIcon.setImageResource(WeatherIcon.SUNNY.iconRes);
            textSky.setText(WeatherIcon.SUNNY.strRes);
        } else if(data.getData().getSky() == 2) {
            imgIcon.setImageResource(WeatherIcon.CLOUDY_1.iconRes);
            textSky.setText(WeatherIcon.CLOUDY_1.strRes);
        } else if(data.getData().getSky() == 3) {
            imgIcon.setImageResource(WeatherIcon.CLOUDY_2.iconRes);
            textSky.setText(WeatherIcon.CLOUDY_2.strRes);
        } else if(data.getData().getSky() == 4) {
            imgIcon.setImageResource(WeatherIcon.CLOUDY_3.iconRes);
            textSky.setText(WeatherIcon.CLOUDY_3.strRes);
        }

        textDegree.setText(String.valueOf(data.getData().getDegree()) + "°C");

        if(data.getData().getPrecipitationType() > 0) {
            // 강수 형태가 있음
            WeatherIcon icon = WeatherIcon.CLOUDY_3;
            if(data.getData().getPrecipitationType() == 1) {
                // 비
                icon = WeatherIcon.getRainIcon(data.getData().getHourlyPrecipitation());
            } else if(data.getData().getPrecipitationType() == 2) {
                // 진눈개비
                icon = WeatherIcon.getRainSnowIcon(data.getData().getHourlyPrecipitation());
            } else if(data.getData().getPrecipitationType() == 3) {
                // 눈
                icon = WeatherIcon.getSnowIcon(data.getData().getHourlyPrecipitation());
            }

            if(data.getData().getThunderbolt() > 0) {
                imgIcon.setImageResource(WeatherIcon.RAIN_LIGHTNING.iconRes);
            } else {
                imgIcon.setImageResource(icon.iconRes);
            }
            textSky.setText(icon.strRes);

        } else {
            if(data.getData().getThunderbolt() > 0) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_LIGHTNING.iconRes);
            }
        }

        textHumidity.setText("습도 : " + data.getData().getHumidity() + "%");
        textRain.setText(data.getData().getHourlyPrecipitation() + "mm/h");
    }
}
