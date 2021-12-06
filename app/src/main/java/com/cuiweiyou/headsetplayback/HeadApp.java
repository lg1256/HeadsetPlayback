package com.cuiweiyou.headsetplayback;

import android.app.Application;

public class HeadApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ConfigUtil.getInstance().init(this);
    }
}
