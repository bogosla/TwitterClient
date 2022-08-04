package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List tweets;

    TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }
    @NonNull
    @Override
    public TweetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTweetBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_tweet, parent, false);
        return new TweetHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Tweet tweet = (Tweet) tweets.get(position);
        ((TweetHolder)holder).bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class TweetHolder extends RecyclerView.ViewHolder {
        public ItemTweetBinding itemRow;

        public TweetHolder(ItemTweetBinding itemRow) {
            super(itemRow.getRoot());
            this.itemRow = itemRow;
        }
        public void bind(Tweet tweet) {
            Glide.with(context).load(tweet.user.profileUrl).into(itemRow.imgProfile);
            itemRow.tvName.setText(tweet.user.name);
            itemRow.tvUsername.setText(String.format("@%s", tweet.user.username));
            itemRow.tvCreatedAt.setText(tweet.createdAt);
            itemRow.tvBody.setText(tweet.body);
            itemRow.executePendingBindings();
        }
    }
}
