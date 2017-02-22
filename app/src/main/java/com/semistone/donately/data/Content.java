package com.semistone.donately.data;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by semistone on 2017-02-21.
 */

public class Content extends RealmObject {
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String FAVORITE = "isFavorite";
    public static final String TYPE_ORG = "type-org";
    public static final String TYPE_PEOPLE = "type-people";

    // TODO: 2017-02-22 프로그레스바, 퍼센트, 목표 금액....!?

    @PrimaryKey
    int id;
    String title;
    String description;
    String description2;
    String pictureUrl;
    String linkUrl;
    String type;
    boolean isFavorite;

    public static int getNextKey(Realm realm) {

        int id;

        try {
            id = realm.where(Content.class).max(Content.ID).intValue() + 1;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
