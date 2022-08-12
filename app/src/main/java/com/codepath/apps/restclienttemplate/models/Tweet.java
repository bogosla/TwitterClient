package com.codepath.apps.restclienttemplate.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Tweet {

    @PrimaryKey
    @ColumnInfo
    public long id;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public String body;

    @Embedded
    public User user;

    @TypeConverters(Converter.class)
    @ColumnInfo
    public List<String> medias = new ArrayList<>();

    public Tweet() {}

    @Ignore
    public Tweet(long id, User user, String body, String createdAt) {
        this.id = id;
        this.user = user;
        this.body = body;
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

    public static Tweet fromJson(JSONObject json) throws JSONException {
        Tweet tweet = new Tweet(
                json.getLong("id"),
                User.fromJson(json.getJSONObject("user")),
                json.getString("text"),
                json.getString("created_at")
        );
        // Takes media for this tweet
        try {
            JSONArray entities_media = json.getJSONObject("extended_entities").getJSONArray("media");
            for (int i = 0; i < entities_media.length(); i++) {
                String m = "";
                m += entities_media.getJSONObject(i).getString("media_url_https");
                m += " - ";
                m += entities_media.getJSONObject(0).getString("type");
                tweet.medias.add(m);
            }

        } catch (Exception e) {e.printStackTrace(); }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            tweets.add(Tweet.fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    @NonNull
    @Override
    public String toString() { return String.format("Tweet(%s){createdAt: %s}", id, createdAt); }

}
