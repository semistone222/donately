package com.semistone.donately.data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by semistone on 2017-02-08.
 */

public class History extends RealmObject {
    public static final String ID = "id";
    public static final String DONATE_DATE = "donateDate";

    @PrimaryKey
    private int id;
    private String userId;
    private int contentId;
    private long donateDate;
    private int adLength;
    private boolean isClicked;

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

    public int getAdLength() {
        return adLength;
    }

    public void setAdLength(int adLength) {
        this.adLength = adLength;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
