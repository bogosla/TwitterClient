package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDAO;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";
    ActivityTimelineBinding biding;
    TweetAdapter adapter;
    List<Tweet> tweets = new ArrayList<>();
    RestClient client;
    RecyclerView rvTweets;
    SwipeRefreshLayout refreshContainer;
    int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        biding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rvTweets = biding.rvTweets;
        refreshContainer = biding.refreshContainer;

        adapter = new TweetAdapter(this, tweets);

        // Refresh listener
        refreshContainer.setOnRefreshListener(() -> {
            populateTimeLine(1);
            refreshContainer.setRefreshing(false);
        });

        // Scroll listener
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int p, int totalItemsCount, RecyclerView view) {
                int new_page = page ++;
                client.getHomeTimeLine(new_page, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                        } catch (JSONException e) { e.printStackTrace(); }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Toast.makeText(TimelineActivity.this, "Maybe no internet connection!!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        // Add decoration to custom each item
        rvTweets.addItemDecoration(new ItemDecorationTweet());
        rvTweets.setLayoutManager(linearLayoutManager); // LayoutManager
        rvTweets.setAdapter(adapter); // Set adapter

        rvTweets.addOnScrollListener(scrollListener);


        client = RestApplication.getRestClient(this);

        populateTimeLine(1);

    }

    public void populateTimeLine(int page) {
        client.getHomeTimeLine(page, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    adapter.clear();
                    List<Tweet> tweetList = Tweet.fromJsonArray(json.jsonArray);
                    adapter.addAll(tweetList);
                    saveTweetsToDB(tweetList.toArray(new Tweet[0]));
                } catch (JSONException e) { e.printStackTrace(); }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                // Toast.makeText(TimelineActivity.this, "Maybe no internet connection!!", Toast.LENGTH_LONG).show();
                getTweetsFromDb();
            }
        });
    }

    public void saveTweetsToDB(Tweet... tweetsToSave) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Tweet, Void, Void> task = new AsyncTask<Tweet, Void, Void>() {
            @Override
            protected Void doInBackground(Tweet... ts) {
                TweetDAO tweetDAO = ((RestApplication) getApplicationContext()).getMyDatabase().getTweetDAO();
                tweetDAO.deleteAll();
                tweetDAO.insertTweet(ts);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                Toast.makeText(TimelineActivity.this, "Save tweets to DB !!", Toast.LENGTH_LONG).show();
            }
        };
        task.execute(tweetsToSave);
    }

    public void getTweetsFromDb() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, List<Tweet>> task = new AsyncTask<Void, Void, List<Tweet>>() {

            @Override
            protected List<Tweet> doInBackground(Void... voids) {
                TweetDAO tweetDAO = ((RestApplication) getApplicationContext()).getMyDatabase().getTweetDAO();
                return tweetDAO.getTweets();
            }

            @Override
            protected void onPostExecute(List<Tweet> ts) {
                Log.i(TAG, "FROM DB " + ts.toString());
                adapter.clear();
                adapter.addAll(ts);
                Toast.makeText(TimelineActivity.this, "Retrieve tweets from DB !!", Toast.LENGTH_LONG).show();
            }
        };
        task.execute();
    }

}