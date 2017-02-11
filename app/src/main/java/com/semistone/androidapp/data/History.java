package com.semistone.androidapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by semistone on 2017-02-08.
 */

public class History extends RealmObject {

    @PrimaryKey
    private int historyId;

    private int userId;

    private long donateDate;

    private String beneficiary;

    private int point;

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getDonateDate() {
        return donateDate;
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

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
