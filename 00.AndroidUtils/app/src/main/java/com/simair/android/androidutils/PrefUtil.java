package com.simair.android.androidutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by simair on 16. 11. 17.
 */

public class PrefUtil {

    private static PrefUtil instance;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    public enum Category {
        GLOBAL,
        ;
    }

    private PrefUtil(Context context, Category category) {
        pref = context.getSharedPreferences(context.getPackageName() + "." + category.name(), Context.MODE_PRIVATE);
        if(pref != null) {
            editor = pref.edit();
        }
    }

    public static PrefUtil getInstance(Context context, Category category) {
        if(instance == null) {
            instance = new PrefUtil(context, category);
        }
        pref = context.getSharedPreferences(context.getPackageName() + "." + category.name(), Context.MODE_PRIVATE);
        if(pref != null) {
            editor = pref.edit();
        }
        return instance;
    }

    public static void clearCategory(Context context, Category category) {
        getInstance(context, category).clear();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    public void putValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putValue(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putValue(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putValue(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putValue(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void putValue(String key, List<String> list) {
        editor.putString(key, new Gson().toJson(list).toString());
        editor.commit();
    }

    public List<String> getStringArray(String key) {
        String json = pref.getString(key, null);
        if(!TextUtils.isEmpty(json)) {
            return new Gson().fromJson(json, List.class);
        }
        return null;
    }

    public Boolean getBoolean(String key, boolean defValue) {
        return pref.getBoolean(key, defValue);
    }

    public Float getFloat(String key, float defValue) {
        return pref.getFloat(key, defValue);
    }

    public Integer getInt(String key, int defValue) {
        return pref.getInt(key, defValue);
    }

    public Long getLong(String key, long defValue) {
        return pref.getLong(key, defValue);
    }

    public String getString(String key, String defValue) {
        if (pref != null) {
            return pref.getString(key, defValue);
        } else {
            return defValue;
        }
    }
}
