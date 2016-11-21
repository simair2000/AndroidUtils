package com.simair.android.androidutils.model;

import com.simair.android.androidutils.network.NetworkException;

import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by simair on 2016-02-19.
 * @param <K> - key data
 * @param <V> - value data
 */
public abstract class DataChain<K extends Serializable, V extends Serializable> {
    private DataChain<K, V> next;   // next chain
    public boolean isNetworkChain = false;

    /**
     * 이 체인이 네트웍 체인임을 선언한다.
     */
    public DataChain<K, V> setNetworkChain() {
        isNetworkChain = true;
        return this;
    }

    /**
     * data chain이 구현해야할 get함수로 key data를 기반으로 value data를 가지고 오는 방식을 구현한다<br>
     * @param key key값
     * @return value data
     * @throws NetworkException
     * @throws JSONException
     */
    protected abstract V getData(K key) throws NetworkException, JSONException;

    /**
     * data chain 값을 조회
     * @param key
     * @return
     * @throws NetworkException
     * @throws JSONException
     */
    public V get(K key) throws NetworkException, JSONException {
        V v = getData(key);
        if(v != null) {
            return v;
        } else if(next == null) {
            return onFail(key);
        } else {
            v = next.get(key);
            if(v == null) {
                return onFail(key);
            } else {
                return onSuccess(key, v);
            }
        }
    }

    public DataChain<K, V> getNext() {
        return next;
    }

    /**
     * next chain을 연결한다
     * @param next 다음체인
     * @return self instance
     */
    public DataChain<K, V> setNextChain(DataChain<K, V> next) {
        this.next = next;
        return this;
    }

    protected V onSuccess(K key, V value) {
        return value;
    }

    protected V onFail(K key) {
        return null;
    }
}
