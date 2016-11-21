package com.simair.android.androidutils.model;

import com.simair.android.androidutils.ErrorCode;
import com.simair.android.androidutils.network.NetworkException;

import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by simair on 2016-02-19.
 */
public class DataFacade<K extends Serializable, V extends Serializable> {
    private final DataChain<K, V> chains;

    public DataFacade(DataChain<K, V> chains) {
        this.chains = chains;
    }

    public V get(K key) throws NetworkException, JSONException {
        return chains.get(key);
    }

    public V getFromNetwork(K key) throws NetworkException, JSONException {
        DataChain<K, V> chain = findNetworkChain();
        if(chain != null) {
            return chain.get(key);
        }
        throw new NetworkException(ErrorCode.ERROR_FACADE);
    }

    private DataChain<K, V> findNetworkChain() {
        DataChain<K, V> tempChain = chains;
        while(tempChain != null) {
            if(tempChain.isNetworkChain) {
                return tempChain;
            }
            tempChain = tempChain.getNext();
        }
        return null;
    }
}
