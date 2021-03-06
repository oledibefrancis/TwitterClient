package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static final String TAG = "TweetAdapter";

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the data at position
        Tweet tweet = tweets.get(position);
        //Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView contentImage;
        TextView lastSeen;
        ImageView commentIcon;
        ImageView likeIcon;
        ImageView likeIconHolder;
        TextView likesCount;
        String username;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            contentImage = itemView.findViewById(R.id.contentImage);
            lastSeen = itemView.findViewById(R.id.time);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            likeIconHolder = itemView.findViewById(R.id.likeIconHolder);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            lastSeen.setText(getRelativeTimeAgo(tweet.createdAt));
            username = tweet.user.screenName;
            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
            if (tweet.imageurl != ""){
                contentImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.imageurl).into(contentImage);
            }
            else {
                contentImage.setVisibility(View.GONE);
            }

            commentIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,CommentActivity.class);
                    intent.putExtra("key",username);
                    context.startActivity(intent);
                }
            });
            likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likeIcon.setVisibility(View.VISIBLE);
                    likeIconHolder.setVisibility(View.GONE);

                }
            }
            );

            likeIconHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        likeIconHolder.setVisibility(View.GONE);
                    likeIcon.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onClick(View view) {
            int postion = getAdapterPosition();
            if (postion != RecyclerView.NO_POSITION){
                Tweet tweet = tweets.get(postion);
                Intent intent = new Intent(context, TweetDetail.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }
    }
    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }
    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

}
