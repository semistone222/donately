package com.semistone.androidapp;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by semistone on 2017-02-09.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
