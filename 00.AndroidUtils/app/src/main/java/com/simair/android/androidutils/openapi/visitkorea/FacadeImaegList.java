package com.simair.android.androidutils.openapi.visitkorea;

import android.content.Context;

import com.simair.android.androidutils.model.CacheManager;
import com.simair.android.androidutils.model.DataChain;
import com.simair.android.androidutils.model.DataFacade;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.openapi.visitkorea.data.ImageListParam;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaImageObject;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by simair on 17. 7. 18.
 */

public class FacadeImaegList extends DataFacade<ImageListParam, ArrayList<VisitKoreaImageObject>> {
    static volatile FacadeImaegList instance;
    static Context context;

    static DataChain<ImageListParam, ArrayList<VisitKoreaImageObject>> cacheChain = new DataChain<ImageListParam, ArrayList<VisitKoreaImageObject>>() {
        @Override
        protected ArrayList<VisitKoreaImageObject> getData(ImageListParam key) throws NetworkException, JSONException, Exception {
            return (ArrayList<VisitKoreaImageObject>) CacheManager.getInstance().get(CacheManager.CacheCategory.CACHE_VISITKOREA_IMAGE, key.toString());
        }

        @Override
        protected ArrayList<VisitKoreaImageObject> onSuccess(ImageListParam key, ArrayList<VisitKoreaImageObject> value) {
            CacheManager.getInstance().insert(context, CacheManager.CacheCategory.CACHE_VISITKOREA_IMAGE, key.toString(), value);
            return value;
        }
    };

    static DataChain<ImageListParam, ArrayList<VisitKoreaImageObject>> networkChain = new DataChain<ImageListParam, ArrayList<VisitKoreaImageObject>>() {
        @Override
        protected ArrayList<VisitKoreaImageObject> getData(ImageListParam key) throws NetworkException, JSONException, Exception {
            return APIVisitKorea.getInstance().requestImageList(key.getContentId(), key.getContentTypeId());
        }
    };

    public FacadeImaegList(DataChain<ImageListParam, ArrayList<VisitKoreaImageObject>> chains) {
        super(chains);
    }

    public static FacadeImaegList getInstance(Context context) {
        if(instance == null) {
            synchronized (FacadeImaegList.class) {
                cacheChain.setNextChain(networkChain);
                instance = new FacadeImaegList(cacheChain);
            }
            instance.context = context;
        }
        return instance;
    }
}
