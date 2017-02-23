package com.semistone.donately.data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by semistone on 2017-02-08.
 */

public class History extends RealmObject {
    public static final String ID = "id";
    public static final String USER_ID = "userId";
    public static final String CONTENT_ID = "contentId";
    public static final String DONATE_DATE = "donateDate";

    @PrimaryKey
    private int id;
    private String userId;
    private int contentId;
    private int advertisementId;
    private long donateDate;
    private boolean isClicked;
    private int point;

    public static int getNextKey(Realm realm) {

        int id;

        try {
            id = realm.where(History.class).max(History.ID).intValue() + 1;
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

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public long getDonateDate() {
        return donateDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDonateDate(long donateDate) {
        this.donateDate = donateDate;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getAdvertisementId() {
        return advertisementId;
    }

    public void setAdvertisementId(int advertisementId) {
        this.advertisementId = advertisementId;
    }
}
