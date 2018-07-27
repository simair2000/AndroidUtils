package com.simair.android.androidutils.openapi.visitkorea;

import android.content.Context;

import com.simair.android.androidutils.model.CacheManager;
import com.simair.android.androidutils.model.DataChain;
import com.simair.android.androidutils.model.DataFacade;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.visitkorea.data.LocationBasedInfoParam;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaLocationBasedListObject;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by simair on 17. 7. 18.
 */

public class FacadeLocationBasedInfo extends DataFacade<LocationBasedInfoParam, ArrayList<VisitKoreaLocationBasedListObject>> {
    static volatile FacadeLocationBasedInfo instance;
    static Context context;

    private static DataChain<LocationBasedInfoParam, ArrayList<VisitKoreaLocationBasedListObject>> cacheChain = new DataChain<LocationBasedInfoParam, ArrayList<VisitKoreaLocationBasedListObject>>() {
        @Override
        protected ArrayList<VisitKoreaLocationBasedListObject> getData(LocationBasedInfoParam key) throws NetworkException, JSONException, Exception {
            return (ArrayList<VisitKoreaLocationBasedListObject>) CacheManager.getInstance().get(CacheManager.CacheCategory.CACHE_VISITKOREA, key.toString());
        }

        @Override
        protected ArrayList<VisitKoreaLocationBasedListObject> onSuccess(LocationBasedInfoParam key, ArrayList<VisitKoreaLocationBasedListObject> value) {
            CacheManager.getInstance().insert(context, CacheManager.CacheCategory.CACHE_VISITKOREA, key.toString(), value);
            return value;
        }
    };

    private static DataChain<LocationBasedInfoParam, ArrayList<VisitKoreaLocationBasedListObject>> networkChain = new DataChain<LocationBasedInfoParam, ArrayList<VisitKoreaLocationBasedListObject>>() {
        @Override
        protected ArrayList<VisitKoreaLocationBasedListObject> getData(LocationBasedInfoParam key) throws NetworkException, JSONException, Exception {
            return APIVisitKorea.getInstance(context).requestLocationBasedInfo(key.getCount(), key.getPageNo(), key.getType(), key.getLongitude(), key.getLatitude(), key.getRange());
        }
    };

    public FacadeLocationBasedInfo(DataChain<LocationBasedInfoParam, ArrayList<VisitKoreaLocationBasedListObject>> chains) {
        super(chains);
    }

    public static FacadeLocationBasedInfo getInstance(Context context) {
        if(instance == null) {
            synchronized (FacadeLocationBasedInfo.class) {
                cacheChain.setNextChain(networkChain);
                instance = new FacadeLocationBasedInfo(cacheChain);
            }
            instance.context = context;
        }
        return instance;
    }
}
