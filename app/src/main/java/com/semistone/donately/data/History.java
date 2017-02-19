package com.semistone.donately.data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by semistone on 2017-02-08.
 */

public class History extends RealmObject {
    public static final String ID = "historyId";
    public static final String DONATE_DATE = "donateDate";

    @PrimaryKey
    private int historyId;

    private String userId;

    private long donateDate;

    private String beneficiary;

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

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
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

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
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
