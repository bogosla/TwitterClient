package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {
    public String createdAt;
    public String body;
    public User user;

    
    public Tweet(User user, String body, String createdAt) {
        this.user = user;
        this.body = body;
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

    public static Tweet fromJson(JSONObject json) throws JSONException {
        return new Tweet(
                User.fromJson(json.getJSONObject("user")),
                json.getString("text"),
                json.getString("created_at")
        );
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            tweets.add(Tweet.fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "createdAt='" + createdAt + '\'' +
                ", body='" + body + '\'' +
                ", user=" + user.toString() +
                '}';
    }
}
