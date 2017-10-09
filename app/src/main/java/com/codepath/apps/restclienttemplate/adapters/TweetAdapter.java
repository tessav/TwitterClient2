package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;

import java.util.List;

/**
 * Created by tessavoon on 9/27/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;
    private TweetAdapterListener mListener;

    public interface TweetAdapterListener {
        public void onItemSelected(View view, int position);
        public void onProfileSelected(View view, int position);
        public void onFavoriteSelected(View view, int position);
        public void onRetweetSelected(View view, int position);
        public void onReplySelected(View view, int position);
    }

    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener) {
        mTweets = tweets;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get data according to position
        final Tweet tweet = mTweets.get(position);
        ParseRelativeDate dateParser = new ParseRelativeDate();

        // populate views according to this data
        holder.tvUserName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText("@" + tweet.user.screenName);
        holder.tvTimeStamp.setText("\u2022 " + dateParser.getRelativeTimeAgo(tweet.createdAt));
        holder.tvRetweet.setText(String.valueOf(tweet.retweetCount));
        holder.tvFavorite.setText(String.valueOf(tweet.favoriteCount));
        holder.ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.twitterGrey));
        holder.tvFavorite.setTextColor(ContextCompat.getColor(context, R.color.twitterGrey));
        holder.ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.twitterGrey));
        holder.tvRetweet.setTextColor(ContextCompat.getColor(context, R.color.twitterGrey));

        if (tweet.isFavorite) {
            holder.ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.favorite));
            holder.tvFavorite.setTextColor(ContextCompat.getColor(context, R.color.favorite));
        }

        if (tweet.isRetweet) {
            holder.ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.retweet));
            holder.tvRetweet.setTextColor(ContextCompat.getColor(context, R.color.retweet));
        }

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .centerCrop().crossFade()
                .transform(new CircleTransform(context))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        ImageView ivRetweet;
        ImageView ivFavorite;
        ImageView ivReply;
        TextView tvUserName;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimeStamp;
        TextView tvRetweet;
        TextView tvFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivLike);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            tvRetweet = (TextView) itemView.findViewById(R.id.tvRetweet);
            tvFavorite = (TextView) itemView.findViewById(R.id.tvLike);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onItemSelected(v, position);
                    }
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onProfileSelected(v, position);
                    }
                }
            });

            ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onReplySelected(v, position);
                    }
                }
            });

            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onFavoriteSelected(v, position);
                        if (tvFavorite.getCurrentTextColor() == ContextCompat.getColor(context, R.color.favorite)) {
                            ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.twitterGrey));
                            tvFavorite.setTextColor(ContextCompat.getColor(context, R.color.twitterGrey));
                            tvFavorite.setText(String.valueOf(Integer.parseInt(tvFavorite.getText().toString()) - 1));
                        } else {
                            ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.favorite));
                            tvFavorite.setTextColor(ContextCompat.getColor(context, R.color.favorite));
                            tvFavorite.setText(String.valueOf(Integer.parseInt(tvFavorite.getText().toString()) + 1));
                        }
                    }
                }
            });

            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onRetweetSelected(v, position);
                        if (tvRetweet.getCurrentTextColor() == ContextCompat.getColor(context, R.color.retweet)) {
                            ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.twitterGrey));
                            tvRetweet.setTextColor(ContextCompat.getColor(context, R.color.twitterGrey));
                            tvRetweet.setText(String.valueOf(Integer.parseInt(tvRetweet.getText().toString()) - 1));
                        } else {
                            ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.retweet));
                            tvRetweet.setTextColor(ContextCompat.getColor(context, R.color.retweet));
                            tvRetweet.setText(String.valueOf(Integer.parseInt(tvRetweet.getText().toString()) + 1));
                        }
                    }
                }
            });
        }

    }
}
