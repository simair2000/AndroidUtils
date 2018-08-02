package com.simair.android.androidutils.openapi.airpollution;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.simair.android.androidutils.model.CacheManager;
import com.simair.android.androidutils.model.DataChain;
import com.simair.android.androidutils.model.DataFacade;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.airpollution.data.AirPollutionObject;
import com.simair.android.androidutils.openapi.airpollution.data.AirPollutionParam;
import com.simair.android.androidutils.openapi.airpollution.data.DustObject;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by simair on 17. 7. 21.
 */

public class FacadeAirPollution extends DataFacade<AirPollutionParam, AirPollutionObject> {

    static volatile FacadeAirPollution instance;
    static Context context;
    static DataChain<AirPollutionParam, AirPollutionObject> cacheChain = new DataChain<AirPollutionParam, AirPollutionObject>() {
        @Override
        protected AirPollutionObject getData(AirPollutionParam key) throws NetworkException, JSONException, Exception {
            return (AirPollutionObject) CacheManager.getInstance().get(CacheManager.CacheCategory.CACHE_AIRPOLLUTION_DUST, key.toString());
        }

        @Override
        protected AirPollutionObject onSuccess(AirPollutionParam key, AirPollutionObject value) {
            CacheManager.getInstance().insert(context, CacheManager.CacheCategory.CACHE_AIRPOLLUTION_DUST, key.toString(), value);
            return value;
        }
    };

    private static final String TAG = FacadeAirPollution.class.getSimpleName();
    static DataChain<AirPollutionParam, AirPollutionObject> networkChain = new DataChain<AirPollutionParam,AirPollutionObject>() {
        @Override
        protected AirPollutionObject getData(AirPollutionParam key) throws NetworkException, JSONException, Exception {
            Pair<Double, Double> tmCoord = APIAirPollution.getTMCoord(key.getThoroughFare(), key.getAdminName(), key.getLocalityName());
            Log.d(TAG, "tmCoord : " + tmCoord.first + ", " + tmCoord.second);
            if(tmCoord != null) {
                String stationName = APIAirPollution.getNearbyStationName(tmCoord.first, tmCoord.second);
                Log.d(TAG, "stationName : " + stationName);
                if(!TextUtils.isEmpty(stationName)) {
                    AirPollutionObject airPollution = APIAirPollution.getAirPollutionInfoByStationName(stationName);
                    Log.i(TAG, "AirPollution : " + airPollution);
                    return airPollution;
                }
            }
            return null;
//            return APIAirPollutionSK.getInstance().getDust(key.getLatitude(), key.getLongitude());
        }
    };

    public FacadeAirPollution(DataChain<AirPollutionParam, AirPollutionObject> chains) {
        super(chains);
    }

    public static FacadeAirPollution getInstance(Context context) {
        if(instance == null) {
            synchronized (FacadeAirPollution.class) {
                cacheChain.setNextChain(networkChain);
                instance = new FacadeAirPollution(cacheChain);
            }
            instance.context = context;
        }
        return instance;
    }
}
