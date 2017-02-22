package com.semistone.donately.data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by semistone on 2017-02-22.
 */

public class VideoAd extends RealmObject {

    public static final String ID = "id";
    public static final String LENGTH = "length";

    @PrimaryKey
    int id;
    int length;
    String fileUrl;
    String promotionUrl;

    public static int getNextKey(Realm realm) {

        int id;

        try {
            id = realm.where(VideoAd.class).max(VideoAd.ID).intValue() + 1;
        } catch (Exception e) {
            id = 0;
        }

        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getPromotionUrl() {
        return promotionUrl;
    }

    public void setPromotionUrl(String promotionUrl) {
        this.promotionUrl = promotionUrl;
    }
}
