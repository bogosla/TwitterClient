package com.codepath.apps.restclienttemplate.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    @ColumnInfo
    public String name;

    @ColumnInfo
    public String username;

    @ColumnInfo
    public Long twitter_id;

    public String profileUrl;

    public User(String name, String username, Long id, String profileUrl) {
        this.name = name;
        this.username = username;
        this.twitter_id = id;
        this.profileUrl = profileUrl;
    }

    public static User fromJson(JSONObject json) throws JSONException {
        return new User(
                json.getString("name"),
                json.getString("screen_name"),
                json.getLong("id"),
                json.getString("profile_image_url_https")
        );
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                '}';
    }
}
