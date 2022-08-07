package com.codepath.apps.restclienttemplate.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
    public Long id;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public String body;

    @Embedded
    public User user;

    public List<Media> medias = new ArrayList<>();

    public static class Media {
        public String url;
        public MediaType type;
    }

    public enum MediaType {
        zero, // represent no photo
        photo,
        video,
        animated_gif
    }

    public Tweet(User user, String body, String createdAt) {
        this.user = user;
        this.body = body;
        this.createdAt = createdAt;
    }

    public String getFormattedCreatedAt() {
        return TimeFormatter.getTimeDifference(createdAt);
    }

    public static Tweet fromJson(JSONObject json) throws JSONException {
        Tweet tweet = new Tweet(
                User.fromJson(json.getJSONObject("user")),
                json.getString("text"),
                json.getString("created_at")
        );
        // Takes media for this tweet
        try {
            JSONArray entities_media = json.getJSONObject("entities_extended").getJSONArray("media");
            for (int i = 0; i < entities_media.length(); i++) {
                Media m = new Media();
                m.url = entities_media.getJSONObject(i).getString("media_url_https");
                String t = entities_media.getJSONObject(0).getString("type");
                switch (t) {
                    case "photo":
                        m.type = MediaType.photo;
                        break;
                    case "video":
                        m.type = MediaType.video;
                        break;
                    case "animated_gif":
                        m.type = MediaType.animated_gif;
                        break;
                    default:
                        m.type = MediaType.zero;
                        break;
                }
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
