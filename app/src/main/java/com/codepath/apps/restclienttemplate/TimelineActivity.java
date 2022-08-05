package com.codepath.apps.restclienttemplate;



import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
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
        refreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeLine(1);
                refreshContainer.setRefreshing(false);
            }
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
                Log.i(TAG, "Requests TIMELINE Success");
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                } catch (JSONException e) { e.printStackTrace(); }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "Requests Failed " + response.toString());
                Toast.makeText(TimelineActivity.this, "Maybe no internet connection!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}