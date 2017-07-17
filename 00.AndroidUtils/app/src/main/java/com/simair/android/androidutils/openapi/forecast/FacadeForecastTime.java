package com.simair.android.androidutils.openapi.forecast;

import android.content.Context;

import com.simair.android.androidutils.model.CacheManager;
import com.simair.android.androidutils.model.DataChain;
import com.simair.android.androidutils.model.DataFacade;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.forecast.data.ForecastTimeObject;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by simair on 17. 7. 17.
 */

public class FacadeForecastTime extends DataFacade<CoordinatesConverter.Coord, HashMap<String, ForecastTimeObject>> {
    static volatile FacadeForecastTime instance;
    static Context context;

    public FacadeForecastTime(DataChain<CoordinatesConverter.Coord, HashMap<String, ForecastTimeObject>> chains) {
        super(chains);
    }

    public static FacadeForecastTime getInstance(Context context) {
        if(instance == null) {
            synchronized (FacadeForecastTime.class) {
                cacheCahin.setNextChain(networkChain);
                instance = new FacadeForecastTime(cacheCahin);
            }
            instance.context = context;
        }
        return instance;
    }

    static DataChain<CoordinatesConverter.Coord, HashMap<String, ForecastTimeObject>> cacheCahin = new DataChain<CoordinatesConverter.Coord, HashMap<String, ForecastTimeObject>>() {

        @Override
        protected HashMap<String, ForecastTimeObject> getData(CoordinatesConverter.Coord key) throws NetworkException, JSONException, Exception {
            String cacheKey = APIForecast.getTodayDate() + APIForecast.getCurrentTime() + String.valueOf((int)key.x) + String.valueOf((int)key.y);
            return (HashMap<String, ForecastTimeObject>) CacheManager.getInstance().get(CacheManager.CacheCategory.CACHE_FORCAST_TIME, cacheKey);
        }

        @Override
        protected HashMap<String, ForecastTimeObject> onSuccess(CoordinatesConverter.Coord key, HashMap<String, ForecastTimeObject> value) {
            String cacheKey = APIForecast.getTodayDate() + APIForecast.getCurrentTime() + String.valueOf((int)key.x) + String.valueOf((int)key.y);
            CacheManager.getInstance().insert(context, CacheManager.CacheCategory.CACHE_FORCAST_TIME, cacheKey, value);
            return value;
        }
    };

    static DataChain<CoordinatesConverter.Coord, HashMap<String, ForecastTimeObject>> networkChain = new DataChain<CoordinatesConverter.Coord, HashMap<String, ForecastTimeObject>>() {

        @Override
        protected HashMap<String, ForecastTimeObject> getData(CoordinatesConverter.Coord key) throws NetworkException, JSONException, Exception {
            return APIForecast.requestTimeData(key.x, key.y);
        }
    };
}
