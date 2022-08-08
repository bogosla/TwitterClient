package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import java.util.Arrays;
import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List tweets;

    TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // Clear all elements
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> items) {
        tweets.addAll(items);
        notifyDataSetChanged();
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

            Glide.with(context).load(tweet.user.profileUrl).transform(new FitCenter(), new CircleCrop()).into(itemRow.imgProfile);
            itemRow.tvName.setText(tweet.user.name);
            itemRow.tvUsername.setText(String.format("@%s", tweet.user.username));
            itemRow.tvCreatedAt.setText(tweet.getFormattedCreatedAt());
            itemRow.tvBody.setText(tweet.body);
            itemRow.imgEmb.setVisibility(ImageView.GONE);

            try {
                List<String> ms = tweet.medias;
                if (!ms.isEmpty()) {
                    List<String> m = Arrays.asList(ms.get(0).split(" - "));
                    if (m.get(1).equals("photo")) {
                        ImageView media = itemRow.imgEmb;
                        media.setVisibility(ImageView.VISIBLE);
                        Glide.with(context).load(m.get(0)).transform(new FitCenter(), new RoundedCorners(12)).into(media);
                    }
                }
            } catch (Exception e) {}

            itemRow.executePendingBindings();
        }
    }
}
