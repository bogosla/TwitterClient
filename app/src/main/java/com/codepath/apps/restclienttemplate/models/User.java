package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

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
    public long userId;

    public String profileUrl;

    public User() {}

    public static User fromJson(JSONObject json) throws JSONException {
        User user = new User();
        user.name = json.getString("name");
        user.username =  json.getString("screen_name");
        user.userId = json.getLong("id");
        user.profileUrl =  json.getString("profile_image_url_https");
        return user;
    }

    @NonNull
    @Override
    public String toString() { return String.format("User(%s){name: %s}", userId, name); }
}
