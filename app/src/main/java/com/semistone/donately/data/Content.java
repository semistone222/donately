package com.semistone.donately.data;

/**
 * Created by semistone on 2017-02-21.
 */

public class Content {

    private int id;
    private String title;
    private String description;
    private String description2;
    private String pictureUrl;
    private String linkUrl;
    private String type;
    private boolean isFavorite;
    private int goal;
    private int currentPoint;

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", description2='" + description2 + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", type='" + type + '\'' +
                ", isFavorite=" + isFavorite +
                ", goal=" + goal +
                ", currentPoint=" + currentPoint +
                '}';
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

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(int currentPoint) {
        this.currentPoint = currentPoint;
    }
}
