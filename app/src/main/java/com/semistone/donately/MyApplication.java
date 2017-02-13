package com.semistone.donately;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by semistone on 2017-02-09.
 */

public class MyApplication extends Application {

    int a;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
