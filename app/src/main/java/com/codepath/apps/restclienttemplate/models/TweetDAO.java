package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDAO {

    @Query("SELECT * FROM Tweet WHERE id = :tweetId")
    Tweet byTweetId(Long tweetId);

    @Query("DELETE FROM Tweet")
    void deleteAll();


    @Query("SELECT * FROM Tweet")
    List<Tweet> getTweets();

    // Replace strategy is needed to ensure an update on the table row.  Otherwise the insertion will
    // fail.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTweet(Tweet... tweets);

}
