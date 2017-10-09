package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.codepath.apps.restclienttemplate.utils.RoundedCornersTransformation;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.ivPostImage) ImageView ivPostImage;
    @BindView(R.id.tvTimeStamp) TextView tvTimeStamp;
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.ivBack) ImageView ivBack;
    @BindView(R.id.fabReply) FloatingActionButton fabReply;
    @BindView(R.id.layout) RelativeLayout rl;
    @BindView(R.id.vvPostVideo) VideoView vvPostVideo;
    @BindView(R.id.scrollView) ScrollView scrollView;
    @BindView(R.id.ivRetweet) ImageView ivRetweet;
    @BindView(R.id.ivLike) ImageView ivLike;
    @BindView(R.id.tvRetweet) TextView tvRetweet;
    @BindView(R.id.tvLike) TextView tvLike;

    private final int REQUEST_CODE = 20;
    Context context;
    Tweet tweet;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context = getApplicationContext();
        ButterKnife.bind(this);
        client = TwitterApp.getRestClient();
        setupToolbar();
        populateView();
        enableBackButton();
        attachFABListener();
        attachFavoriteListener();
        attachRetweetListener();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    private void populateView() {
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        populateTweetDetails(tweet);
        populateMedia(tweet);
    }

    private void populateTweetDetails(Tweet tweet) {
        tvUserName.setText(tweet.user.name);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);
        tvLike.setText(String.valueOf(tweet.favoriteCount));
        tvRetweet.setText(String.valueOf(tweet.retweetCount));
        ParseRelativeDate dateParser = new ParseRelativeDate();
        tvTimeStamp.setText(dateParser.getRelativeTimeAgo(tweet.createdAt));
        if (tweet.isFavorite) {
            ivLike.setColorFilter(ContextCompat.getColor(context, R.color.favorite));
            tvLike.setTextColor(ContextCompat.getColor(context, R.color.favorite));
        } else {
            ivLike.setColorFilter(ContextCompat.getColor(context, R.color.twitterGrey));
            tvLike.setTextColor(ContextCompat.getColor(context, R.color.twitterGrey));
        }
        if (tweet.isRetweet) {
            ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.retweet));
            tvRetweet.setTextColor(ContextCompat.getColor(context, R.color.retweet));
        } else {
            ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.twitterGrey));
            tvRetweet.setTextColor(ContextCompat.getColor(context, R.color.twitterGrey));
        }
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .centerCrop().crossFade()
                .transform(new CircleTransform(context))
                .into(ivProfileImage);
    }

    private void populateMedia(Tweet tweet) {
        if (tweet.mediaType == Tweet.MediaType.IMAGE) {
            Glide.with(context)
                    .load(tweet.mediaUrl)
                    .crossFade()
                    .transform(new RoundedCornersTransformation(context, 25, 0))
                    .into(ivPostImage);
        } else if (tweet.mediaType == Tweet.MediaType.VIDEO) {
            ivPostImage.setVisibility(View.INVISIBLE);
            scrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            vvPostVideo.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(tweet.mediaUrl);
            vvPostVideo.setVideoURI(uri);
            vvPostVideo.setZOrderOnTop(true);
            vvPostVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    vvPostVideo.start();
                }
            });
        } else {
            ivPostImage.setAdjustViewBounds(false);
        }
    }

    private void attachRetweetListener() {
        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.isRetweet) {
                    unretweet(tweet);
                    tweet.isRetweet = false;
                    ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.twitterGrey));
                    tvRetweet.setTextColor(ContextCompat.getColor(context, R.color.twitterGrey));
                    tvRetweet.setText(String.valueOf(Integer.parseInt(tvRetweet.getText().toString()) - 1));
                } else {
                    retweet(tweet);
                    tweet.isRetweet = true;
                    ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.retweet));
                    tvRetweet.setTextColor(ContextCompat.getColor(context, R.color.retweet));
                    tvRetweet.setText(String.valueOf(Integer.parseInt(tvRetweet.getText().toString()) + 1));
                }
            }
        });
    }

    private void attachFavoriteListener() {
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.isFavorite) {
                    unfavorite(tweet);
                    tweet.isFavorite = false;
                    ivLike.setColorFilter(ContextCompat.getColor(context, R.color.twitterGrey));
                    tvLike.setTextColor(ContextCompat.getColor(context, R.color.twitterGrey));
                    tvLike.setText(String.valueOf(Integer.parseInt(tvLike.getText().toString()) - 1));
                } else {
                    favorite(tweet);
                    tweet.isFavorite = true;
                    ivLike.setColorFilter(ContextCompat.getColor(context, R.color.favorite));
                    tvLike.setTextColor(ContextCompat.getColor(context, R.color.favorite));
                    tvLike.setText(String.valueOf(Integer.parseInt(tvLike.getText().toString()) + 1));
                }
            }
        });
    }


    private void favorite(Tweet tweet) {
        client.favoriteTweet(tweet, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("twitterclient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TWITTERCLIENT", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    private void unfavorite(Tweet tweet) {
        client.unfavoriteTweet(tweet, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("twitterclient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TWITTERCLIENT", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    private void retweet(Tweet tweet) {
        client.retweet(tweet, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("twitterclient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TWITTERCLIENT", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    private void unretweet(Tweet tweet) {
        client.unretweet(tweet, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("twitterclient", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TWITTERCLIENT", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }


    private void enableBackButton() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void attachFABListener() {
        fabReply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                composeTweet();
            }
        });
    }

    private void composeTweet() {
        Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
        i.putExtra("isReply", true);
        i.putExtra("statusId", String.valueOf(tweet.uid));
        i.putExtra("screenName", tweet.user.screenName);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Snackbar.make(rl, R.string.finish_compose, Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}
