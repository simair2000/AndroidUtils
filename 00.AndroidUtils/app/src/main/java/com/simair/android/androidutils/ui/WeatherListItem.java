package com.simair.android.androidutils.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
    private View bg;

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
        bg = findViewById(R.id.bg);
        textDate = (TextView)findViewById(R.id.textDate);
        imgIcon = (ImageView)findViewById(R.id.imgIcon);
        textSky = (TextView)findViewById(R.id.textSky);
        textDegree = (TextView)findViewById(R.id.textDegree);
        textHumidity = (TextView)findViewById(R.id.textHumidity);
        textRain = (TextView)findViewById(R.id.textRain);
    }

    public void setData(ForecastTimeObject data) {
        Date time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            String dateTime = data.getForecastDate() + data.getForecastTime();
            time = sdf.parse(dateTime);
            sdf = new SimpleDateFormat("MM/dd HH:mm");
            textDate.setText(sdf.format(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(sdf.format(time));

        if(data.getData().getSky() == 1) {
            imgIcon.setImageResource(WeatherIcon.SUNNY.getTimedIcon(hour));
            textSky.setText(WeatherIcon.SUNNY.strRes);
        } else if(data.getData().getSky() == 2) {
            imgIcon.setImageResource(WeatherIcon.CLOUDY_1.getTimedIcon(hour));
            textSky.setText(WeatherIcon.CLOUDY_1.strRes);
        } else if(data.getData().getSky() == 3) {
            imgIcon.setImageResource(WeatherIcon.CLOUDY_2.getTimedIcon(hour));
            textSky.setText(WeatherIcon.CLOUDY_2.strRes);
        } else if(data.getData().getSky() == 4) {
            imgIcon.setImageResource(WeatherIcon.CLOUDY_3.getTimedIcon(hour));
            textSky.setText(WeatherIcon.CLOUDY_3.strRes);
        }

        float degree = data.getData().getDegree();
        if(degree == 0) {
            degree = data.getData().getDegree3();
        }
        textDegree.setText(String.valueOf(degree) + "°C");
        setBGColor(degree);

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
                imgIcon.setImageResource(icon.getTimedIcon(hour));
            }
            textSky.setText(icon.strRes);

        } else {
            if(data.getData().getThunderbolt() > 0) {
                imgIcon.setImageResource(WeatherIcon.CLOUDY_LIGHTNING.getTimedIcon(hour));
            }
        }

        textHumidity.setText("습도 : " + data.getData().getHumidity() + "%");
//        textRain.setText(data.getData().getHourlyPrecipitation() + "mm/h");
        textRain.setText(data.getData().getPrecipitationProbability() + "%");
    }

    /**
     * 기온에 따라 background color 값 설정
     * @param degree
     */
    private void setBGColor(float degree) {
        String rgb = "000000";
        if(degree < 0) {
            rgb = "4c00ff";
        } else if(0 <= degree && degree < 2) {
            rgb = "0c00ff";
        } else if(2 <= degree && degree < 4) {
            rgb = "0021ff";
        } else if(4 <= degree && degree < 6) {
            rgb = "0055ff";
        } else if(6 <= degree && degree < 8) {
            rgb = "007fff";
        } else if(8 <= degree && degree < 10) {
            rgb = "00a9ff";
        } else if(10 <= degree && degree < 12) {
            rgb = "00d4ff";
        } else if(12 <= degree && degree < 14) {
            rgb = "00fffa";
        } else if(14 <= degree && degree < 16) {
            rgb = "00ffd4";
        } else if(16 <= degree && degree < 18) {
            rgb = "00ff99";
        } else if(18 <= degree && degree < 20) {
            rgb = "00ff5d";
        } else if(20 <= degree && degree < 22) {
            rgb = "00ff21";
        } else if(22 <= degree && degree < 24) {
            rgb = "37ff00";
        } else if(24 <= degree && degree < 26) {
            rgb = "bfff00";
        } else if(26 <= degree && degree < 28) {
            rgb = "fffa00";
        } else if(28 <= degree && degree < 30) {
            rgb = "ffc700";
        } else if(30 <= degree && degree < 32) {
            rgb = "ff9d00";
        } else if(32 <= degree && degree < 34) {
            rgb = "ff6e00";
        } else if(34 <= degree && degree < 36) {
            rgb = "ff3f00";
        } else if(36 <= degree && degree < 38) {
            rgb = "ff2a00";
        } else if(38 <= degree) {
            rgb = "ff0000";
        }
        bg.setBackgroundColor(Color.parseColor("#50" + rgb));
    }
}
