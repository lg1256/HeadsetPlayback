package com.cuiweiyou.headsetplayback;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtil {

    private static ConfigUtil instance;
    private SharedPreferences preferences;

    private ConfigUtil() {

    }

    public static ConfigUtil getInstance() {
        if (null == instance) {
            synchronized ((ConfigUtil.class)) {
                if (null == instance) {
                    instance = new ConfigUtil();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        preferences = context.getSharedPreferences("", Context.MODE_PRIVATE);
    }

    public void setAutoRecord(boolean auto) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean("AutoRecord", auto);
        edit.commit();
    }

    public boolean getAutoRecord() {
        return preferences.getBoolean("AutoRecord", true);
    }
}
