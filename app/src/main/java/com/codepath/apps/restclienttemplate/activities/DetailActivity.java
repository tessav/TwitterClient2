package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.codepath.apps.restclienttemplate.utils.RoundedCornersTransformation;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private final int REQUEST_CODE = 20;
    Context context;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context = getApplicationContext();
        ButterKnife.bind(this);
        setupToolbar();
        populateView();
        enableBackButton();
        attachFABListener();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    private void populateView() {
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvUserName.setText(tweet.user.name);
        tvScreenName.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);
        ParseRelativeDate dateParser = new ParseRelativeDate();
        tvTimeStamp.setText(dateParser.getRelativeTimeAgo(tweet.createdAt));
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .centerCrop().crossFade()
                .transform(new CircleTransform(context))
                .into(ivProfileImage);
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
        }
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
