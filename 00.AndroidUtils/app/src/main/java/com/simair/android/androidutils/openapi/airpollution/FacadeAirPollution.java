package com.simair.android.androidutils.openapi.airpollution;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.simair.android.androidutils.Utils;
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

public class FacadeAirPollution extends DataFacade<AirPollutionParam, ArrayList<DustObject>> {

    static volatile FacadeAirPollution instance;
    static Context context;
    static DataChain<AirPollutionParam, ArrayList<DustObject>> cacheChain = new DataChain<AirPollutionParam, ArrayList<DustObject>>() {
        @Override
        protected ArrayList<DustObject> getData(AirPollutionParam key) throws NetworkException, JSONException, Exception {
            return (ArrayList<DustObject>) CacheManager.getInstance().get(CacheManager.CacheCategory.CACHE_AIRPOLLUTION_DUST, key.toString());
        }

        @Override
        protected ArrayList<DustObject> onSuccess(AirPollutionParam key, ArrayList<DustObject> value) {
            CacheManager.getInstance().insert(context, CacheManager.CacheCategory.CACHE_AIRPOLLUTION_DUST, key.toString(), value);
            return value;
        }
    };

    private static final String TAG = FacadeAirPollution.class.getSimpleName();
    static DataChain<AirPollutionParam, ArrayList<DustObject>> networkChain = new DataChain<AirPollutionParam, ArrayList<DustObject>>() {
        @Override
        protected ArrayList<DustObject> getData(AirPollutionParam key) throws NetworkException, JSONException, Exception {
            Pair<Double, Double> tmCoord = APIAirPollution.getTMCoord(key.getThoroughFare(), key.getAdminName(), key.getLocalityName());
            Log.d(TAG, "tmCoord : " + tmCoord.first + ", " + tmCoord.second);
            if(tmCoord != null) {
                String stationName = APIAirPollution.getNearbyStationName(tmCoord.first, tmCoord.second);
                Log.d(TAG, "stationName : " + stationName);
                if(!TextUtils.isEmpty(stationName)) {
                    AirPollutionObject airPollution = APIAirPollution.getAirPollutionInfoByStationName(stationName);
                    Log.i(TAG, "AirPollution : " + airPollution);
                }
            }
            return null;
//            return APIAirPollutionSK.getInstance().getDust(key.getLatitude(), key.getLongitude());
        }
    };

    public FacadeAirPollution(DataChain<AirPollutionParam, ArrayList<DustObject>> chains) {
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
