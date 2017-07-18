package com.simair.android.androidutils.openapi.visitkorea;

import android.content.Context;

import com.simair.android.androidutils.model.CacheManager;
import com.simair.android.androidutils.model.DataChain;
import com.simair.android.androidutils.model.DataFacade;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaDetailCommonObject;

import org.json.JSONException;

/**
 * Created by simair on 17. 7. 18.
 */

public class FacadeDetailCommon extends DataFacade<Long, VisitKoreaDetailCommonObject> {
    static volatile FacadeDetailCommon instance;
    static Context context;
    static DataChain<Long, VisitKoreaDetailCommonObject> cacheChain = new DataChain<Long, VisitKoreaDetailCommonObject>() {
        @Override
        protected VisitKoreaDetailCommonObject getData(Long key) throws NetworkException, JSONException, Exception {
            return (VisitKoreaDetailCommonObject) CacheManager.getInstance().get(CacheManager.CacheCategory.CACHE_VISITKOREA_DETAIL, key);
        }

        @Override
        protected VisitKoreaDetailCommonObject onSuccess(Long key, VisitKoreaDetailCommonObject value) {
            CacheManager.getInstance().insert(context, CacheManager.CacheCategory.CACHE_VISITKOREA_DETAIL, key, value);
            return value;
        }
    };
    static DataChain<Long, VisitKoreaDetailCommonObject> networkChain = new DataChain<Long, VisitKoreaDetailCommonObject>() {
        @Override
        protected VisitKoreaDetailCommonObject getData(Long key) throws NetworkException, JSONException, Exception {
            return APIVisitKorea.getInstance().requestDetailCommon(key);
        }
    };

    public FacadeDetailCommon(DataChain<Long, VisitKoreaDetailCommonObject> chains) {
        super(chains);
    }

    public static FacadeDetailCommon getInstance(Context context) {
        if(instance == null) {
            synchronized (FacadeDetailCommon.class) {
                cacheChain.setNextChain(networkChain);
                instance = new FacadeDetailCommon(cacheChain);
            }
            instance.context = context;
        }
        return instance;
    }
}
