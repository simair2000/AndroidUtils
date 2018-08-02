package com.simair.android.androidutils.openapi.forecast;

import android.content.Context;

import com.simair.android.androidutils.model.CacheManager;
import com.simair.android.androidutils.model.DataChain;
import com.simair.android.androidutils.model.DataFacade;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.forecast.data.ForecastCurrentObject;

import org.json.JSONException;

/**
 * Created by simair on 17. 7. 14.
 */

public class FacadeForecastCurrent extends DataFacade<CoordinatesConverter.Coord, ForecastCurrentObject> {

    static volatile FacadeForecastCurrent instance;
    static Context context;

    public FacadeForecastCurrent(DataChain<CoordinatesConverter.Coord, ForecastCurrentObject> chains) {
        super(chains);
    }

    public static FacadeForecastCurrent getInstance(Context context) {
        if(instance == null) {
            synchronized (FacadeForecastCurrent.class) {
                cacheChain.setNextChain(networkChain);
                instance = new FacadeForecastCurrent(cacheChain);
            }
            instance.context = context;
        }
        return instance;
    }

    static DataChain<CoordinatesConverter.Coord, ForecastCurrentObject> cacheChain = new DataChain<CoordinatesConverter.Coord, ForecastCurrentObject>() {
        @Override
        protected ForecastCurrentObject getData(CoordinatesConverter.Coord key) throws NetworkException, JSONException, Exception {
            String cacheKey = APIForecast.getTodayDate() + APIForecast.getCurrentTime() + String.valueOf((int)key.x) + String.valueOf((int)key.y);
            return (ForecastCurrentObject) CacheManager.getInstance().get(CacheManager.CacheCategory.CACHE_FORCAST_CURRENT, cacheKey);
        }

        @Override
        protected ForecastCurrentObject onSuccess(CoordinatesConverter.Coord key, ForecastCurrentObject value) {
            String cacheKey = APIForecast.getTodayDate() + APIForecast.getCurrentTime() + String.valueOf((int)key.x) + String.valueOf((int)key.y);
            CacheManager.getInstance().insert(context, CacheManager.CacheCategory.CACHE_FORCAST_CURRENT, cacheKey, value, 60000);
            return value;
        }
    };

    static DataChain<CoordinatesConverter.Coord, ForecastCurrentObject> networkChain = new DataChain<CoordinatesConverter.Coord, ForecastCurrentObject>() {
        @Override
        protected ForecastCurrentObject getData(CoordinatesConverter.Coord key) throws NetworkException, JSONException, Exception {
            return APIForecast.requestCurrent(key.x, key.y);
        }
    };


}
