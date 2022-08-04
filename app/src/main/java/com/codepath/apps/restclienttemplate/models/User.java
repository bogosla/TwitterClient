package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String name;
    public String username;
    public String profileUrl;

    public User(String name, String username, String profileUrl) {
        this.name = name;
        this.username = username;
        this.profileUrl = profileUrl;
    }
    public static User fromJson(JSONObject json) throws JSONException {
        return new User(
                json.getString("name"),
                json.getString("screen_name"),
                json.getString("profile_image_url_https")
        );
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                '}';
    }
}
