package com.simair.android.androidutils.model;

import android.app.ActivityManager;
import android.content.Context;
import android.util.LruCache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 앱이 살아있을때에만 저장하고 있는 캐시 데이터를 관리하는 클래스<br>
 * 앱 종료시 캐시는 사라진다<br>
 * Created by simair on 2016-02-19.
 */
public class CacheManager {

    public enum CacheCategory {
        CACHE_DEFAULT,
        CACHE_GLOBAL,
        CACHE_FORCAST_CURRENT,
        CACHE_FORCAST_TIME,
        CACHE_VISITKOREA,
        CACHE_VISITKOREA_DETAIL,
        CACHE_VISITKOREA_IMAGE,
        CACHE_AIRPOLLUTION_DUST,
        ;
    }

    private static volatile CacheManager instance = null;

    private CacheManager() {}

//    private Map<CacheCategory, Map<Serializable, Serializable>> caches = new HashMap<>();
    private Map<CacheCategory, LruCache<Serializable, Serializable>> caches = new HashMap<>();

    /**
     * 계속해서 캐시에 저장되어 Out of memory상황이 발생하는 것을 방지하기 위하여 캐시에 저장할 최대 용량을 계산한다
     * @return 최대 캐시 사이즈
     */
    private int calcMaxCacheSize(Context context) {
        int memClass = ((ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE))).getMemoryClass();
        int heapSize = 1024 * 1024 * memClass;
        int cacheSize = heapSize / 8 / CacheCategory.values().length; // 힙 사이즈의 1/8을 캐시에 사용한다
        return cacheSize;
    }

    public static CacheManager getInstance() {
        if(instance == null) {
            synchronized (CacheManager.class) {
                instance = new CacheManager();
            }
        }
        return instance;
    }

    /**
     * Default type cache insert
     * @param key
     * @param value
     */
    public void insert(Context context, Serializable key, Serializable value) {
        insert(context, CacheCategory.CACHE_DEFAULT, key, value);
    }

    /**
     * Default type cache get
     * @param key
     * @return
     */
    public Serializable get(Serializable key) {
        return get(CacheCategory.CACHE_DEFAULT, key);
    }

    /**
     * Default type cache delete
     * @param key
     */
    public void delete(Serializable key) {
        deleteCache(CacheCategory.CACHE_DEFAULT, key);
    }

    /**
     * insert cache data to the specific category
     * @param category cache type
     * @param key
     * @param value
     */
    public void insert(Context context, CacheCategory category, Serializable key, Serializable value) {
        LruCache<Serializable, Serializable> cache = caches.get(category);
        if(cache == null) {
            cache = new LruCache<>(calcMaxCacheSize(context));
            caches.put(category, cache);
        }
        cache.put(key, value);
    }

    /**
     * 데이터를 특정 시간동안 임시로 캐시에 저장한다.
     * @param category
     * @param key
     * @param value
     * @param milliseconds 저장하고 있는 시간. 이시간이 지나면 자동으로 캐시가 삭제됨
     */
    public void insert(Context context, final CacheCategory category, final Serializable key, Serializable value, int milliseconds) {
        LruCache<Serializable, Serializable> cache = caches.get(category);
        if(cache == null) {
            cache = new LruCache<>(calcMaxCacheSize(context));
            caches.put(category, cache);
        }
        cache.put(key, value);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                deleteCache(category, key);
            }
        }, milliseconds);
    }

    /**
     * get a cache data from the specific category
     * @param category
     * @param key
     * @return
     */
    public Serializable get(CacheCategory category, Serializable key) {
        LruCache<Serializable, Serializable> cache = caches.get(category);
        if(cache != null) {
            return cache.get(key);
        }
        return null;
    }

    /**
     * delete all cache datas
     */
    public void deleteAllCaches() {
        caches.clear();
    }

    /**
     * delete a cache data from the specific category
     * @param category
     * @param key
     */
    public void deleteCache(CacheCategory category, Serializable key) {
        LruCache<Serializable, Serializable> cache = caches.get(category);
        if(cache != null) {
            cache.remove(key);
        }
    }
}
