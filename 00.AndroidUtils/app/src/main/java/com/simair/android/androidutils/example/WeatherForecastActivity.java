package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.forecast.APIForecast;
import com.simair.android.androidutils.openapi.forecast.CoordinatesConverter;
import com.simair.android.androidutils.openapi.forecast.data.ForecastTodayObject;

import org.json.JSONException;

public class WeatherForecastActivity extends AppCompatActivity implements Command.CommandListener {

    private static final String TAG = WeatherForecastActivity.class.getSimpleName();
    private Command commandTest;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, WeatherForecastActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        initView();
    }

    private void initView() {
        CoordinatesConverter.UTMK utmk = CoordinatesConverter.getInstance().geo2utmk((float) 37.405148, (float) 127.097248);
        Log.e("simair", "geo2utmk [37.405148, 127.097248] : " + utmk.x + ", " + utmk.y);
        CoordinatesConverter.GeoCode geo = CoordinatesConverter.getInstance().utmk2geo((float) 60.905716, (float) 122.235306);
        Log.e("simair", "utmk2geo [60.905716, 122.235306] : " + geo.latitude + ", " + geo.longitude);

        requestTest();
    }

    private void requestTest() {
        commandTest = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException, Exception {
                ForecastTodayObject response = APIForecast.requestToday((float) 60.905716, (float) 122.235306);
                data.putSerializable("response", response);
            }
        }.setOnCommandListener(this).showWaitDialog(this).start();
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        if(command == commandTest) {
            Log.d(TAG, data.getSerializable("response").toString());
        }
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {
        Log.e(TAG, "errorCode[" + errorCode + "] : " + errorMessage);
    }
}
