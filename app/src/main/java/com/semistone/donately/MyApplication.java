package com.semistone.donately;

import android.app.Application;
import android.util.Log;

import com.semistone.donately.data.Advertisement;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by semistone on 2017-02-09.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        // TEST 로그아웃 할때 다 없어지는 점 고려하자. 서버를 안 쓸거면...
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Advertisement> results = realm.where(Advertisement.class).findAll();
        Log.e("bbbb", "onCreate: " + results.toString());
        if (results == null || results.isEmpty()) {
            Log.e("cccc", "onCreate: ");
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Advertisement advertisement = realm.createObject(Advertisement.class, Advertisement.getNextKey(realm));
                    advertisement.setLength(15);
                    advertisement.setFileUrl("android.resource://" + getPackageName() + "/" + R.raw.ad_15);
                    advertisement.setPromotionUrl("http://www.naver.com");
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Advertisement advertisement = realm.createObject(Advertisement.class, Advertisement.getNextKey(realm));
                    advertisement.setLength(30);
                    advertisement.setFileUrl("android.resource://" + getPackageName() + "/" + R.raw.ad_30);
                    advertisement.setPromotionUrl("https://www.google.com");
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Advertisement advertisement = realm.createObject(Advertisement.class, Advertisement.getNextKey(realm));
                    advertisement.setLength(60);
                    advertisement.setFileUrl("android.resource://" + getPackageName() + "/" + R.raw.ad_60);
                    advertisement.setPromotionUrl("https://slack.com/");
                }
            });

        }
        realm.close();
    }
}
