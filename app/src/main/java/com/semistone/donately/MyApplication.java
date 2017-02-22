package com.semistone.donately;

import android.app.Application;
import android.util.Log;

import com.semistone.donately.data.VideoAd;

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

        // TEST
        Realm realm = Realm.getDefaultInstance();
        RealmResults<VideoAd> results = realm.where(VideoAd.class).findAll();
        Log.e("bbbb", "onCreate: " + results.toString());
        if (results == null || results.isEmpty()) {
            Log.e("cccc", "onCreate: ");
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    VideoAd videoAd = realm.createObject(VideoAd.class, VideoAd.getNextKey(realm));
                    videoAd.setLength(15);
                    videoAd.setFileUrl("android.resource://" + getPackageName() + "/" + R.raw.ad_15);
                    videoAd.setPromotionUrl("http://www.naver.com");
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    VideoAd videoAd = realm.createObject(VideoAd.class, VideoAd.getNextKey(realm));
                    videoAd.setLength(30);
                    videoAd.setFileUrl("android.resource://" + getPackageName() + "/" + R.raw.ad_30);
                    videoAd.setPromotionUrl("https://www.google.com");
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    VideoAd videoAd = realm.createObject(VideoAd.class, VideoAd.getNextKey(realm));
                    videoAd.setLength(60);
                    videoAd.setFileUrl("android.resource://" + getPackageName() + "/" + R.raw.ad_60);
                    videoAd.setPromotionUrl("https://slack.com/");
                }
            });

        }
        realm.close();
    }
}
