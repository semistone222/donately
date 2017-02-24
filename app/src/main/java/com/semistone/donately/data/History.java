package com.semistone.donately.data;

/**
 * Created by semistone on 2017-02-08.
 */

public class History {

    private int id;
    private String userId;
    private int contentId;
    private int advertisementId;
    private long donateDate;
    private boolean isClicked;
    private int point;
    private String title;

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", contentId=" + contentId +
                ", advertisementId=" + advertisementId +
                ", donateDate=" + donateDate +
                ", isClicked=" + isClicked +
                ", point=" + point +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
