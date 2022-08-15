package com.codepath.apps.restclienttemplate;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.codepath.apps.restclienttemplate.models.Converter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDAO;

@Database(entities={Tweet.class}, version=5)
@TypeConverters(Converter.class)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TweetDAO getTweetDAO();

    // Database name to be used
    public static final String NAME = "TweetDB";
}
