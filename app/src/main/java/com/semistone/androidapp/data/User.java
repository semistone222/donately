package com.semistone.androidapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by semistone on 2017-02-08.
 */

public class User extends RealmObject {

    public static final String ID = "id";

    @PrimaryKey
    private String id;

    private String name;

    private String email;

    private String accessToken;

    private String type;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
