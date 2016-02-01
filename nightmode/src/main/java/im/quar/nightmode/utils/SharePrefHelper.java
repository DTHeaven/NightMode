package im.quar.nightmode.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DTHeaven on 16/2/1.
 */
public class SharePrefHelper {
    private SharedPreferences prefs = null;
    private static SharePrefHelper sharePrefHelper = null;

    private SharePrefHelper(Context cxt, String name) {
        prefs = cxt.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static SharePrefHelper getInstance(Context context, String name) {
        if (sharePrefHelper == null) {
            sharePrefHelper = new SharePrefHelper(context, name);
        }
        return sharePrefHelper;
    }

    public static SharePrefHelper newInstance(Context context, String name) {
        return new SharePrefHelper(context, name);
    }

    public void setPref(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setPref(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setPref(String key, float value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public void setPref(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setPref(String key, long value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public boolean getPref(String key, Boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public String getPref(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public int getPref(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    public long getPref(String key, long defaultValue) {
        return prefs.getLong(key, defaultValue);
    }

    public float getPref(String key, float defaultValue) {
        return prefs.getFloat(key, defaultValue);
    }

    public boolean hasPrefWithKey(String key) {
        return prefs.contains(key);
    }

    public void removePref(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
