package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;

import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    RecyclerView rvTweets;

    List<Tweet> tweets = new ArrayList<>();
    RestClient client;
    TweetDAO tweetDAO;
    SwipeRefreshLayout refreshContainer;


    List<String> toDown = new ArrayList<>();
    Downloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        biding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        Toolbar toolbar = biding.toolbar;
        setSupportActionBar(toolbar);

        downloader = new Downloader(TimelineActivity.this);
        downloader.setVideoDownloadListener(new Downloader.VideoDownloadListener() {
            @Override
            public void onVideoDownloaded(String url, String path) {
                Log.i(TAG, url + " Downloaded at " + path);
            }

            @Override
            public void onVideoFailed(String url) {
                Log.i(TAG, url + " Failed !!");

            }
        });

        toDown.add("https://i.imgur.com/OEUKLCm.png");
        toDown.add("https://i.imgur.com/cZEhJkg.gif");
        toDown.add("https://i.imgur.com/P5BAPDb.gif");

        biding.fabCompose.setOnClickListener(view -> showCompose());
        
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        
        rvTweets = biding.rvTweets;
        refreshContainer = biding.refreshContainer;

        adapter = new TweetAdapter(this, tweets);

        // Refresh listener
        refreshContainer.setOnRefreshListener(() -> {
            populateTimeLine();
            refreshContainer.setRefreshing(false);
        });

        // Scroll listener
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int p, int totalItemsCount, RecyclerView view) {
                client.nextPageOfTweets(tweets.get(tweets.size() - 1).id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        List<Tweet> tweetList = null;
                        try {
                            tweetList = Tweet.fromJsonArray(json.jsonArray);
                            adapter.addAll(tweetList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });

            }
        };

        rvTweets.setLayoutManager(linearLayoutManager); // LayoutManager
        rvTweets.setAdapter(adapter); // Set adapter
        rvTweets.setHasFixedSize(true);

        rvTweets.addOnScrollListener(scrollListener);

        client = RestApplication.getRestClient(this); // Client
        tweetDAO = ((RestApplication) getApplicationContext()).getMyDatabase().getTweetDAO(); // DAO

        populateTimeLine(); // fetch data

        FileCache f = new FileCache(TimelineActivity.this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // downloader.clear();
        // downloader.start(toDown);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate( R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                // clear session current user
                return true;
            case R.id.compose:
                showCompose();
                // Start compose
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void populateTimeLine() {
        client.getHomeTimeLine(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    adapter.clear();
                    List<Tweet> tweetList = Tweet.fromJsonArray(json.jsonArray);
                    Log.i(TAG, tweetList.toString());
                    adapter.addAll(tweetList);
                    saveTweetsToDB(tweetList.toArray(new Tweet[0])); // Save to DB
                } catch (JSONException e) { e.printStackTrace(); }
            }
            
            // Retrieve tweets saved in DB if failed!!
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) { getTweetsFromDb();}
        });
    }

    public void saveTweetsToDB(Tweet... tweetsToSave) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Tweet, Void, Void> task = new AsyncTask<Tweet, Void, Void>() {

            @Override
            protected Void doInBackground(Tweet... lists) {
                tweetDAO.deleteAll();
                tweetDAO.insertTweet(tweetsToSave);
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
                tweetDAO = ((RestApplication) getApplicationContext()).getMyDatabase().getTweetDAO();
                return tweetDAO.getTweets();
            }

            @Override
            protected void onPostExecute(List<Tweet> ts) {
                adapter.clear();
                adapter.addAll(ts);
                Toast.makeText(TimelineActivity.this, "Retrieve tweets from DB !!", Toast.LENGTH_LONG).show();
            }
        };
        task.execute();
    }

    private void showCompose() {
        FragmentTransaction fManager = getSupportFragmentManager().beginTransaction();
        fManager.addToBackStack(null);
        ComposeFragment compose = ComposeFragment.newInstance("New Tweet");
        compose.setCancelable(false);

        compose.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        compose.show(fManager, "compose_fragment");
    }
    public void showAlert(String d) {
        FragmentManager fm = getSupportFragmentManager();
        AlertFragment alert = AlertFragment.newInstance("Persistence");
        alert.draft = d;
        alert.show(fm, "alert_fragment");
    }

    public void postTweet(String text) {
        client.createTweet(text, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    tweets.add(0, Tweet.fromJson(json.jsonObject));
                    adapter.notifyItemInserted(0);
                    rvTweets.scrollToPosition(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                runOnUiThread(() -> Toast.makeText(TimelineActivity.this, "Failed !!", Toast.LENGTH_LONG).show());
            }
        });
    }

}