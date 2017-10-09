package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    TwitterClient twitterClient;
    Context context;
    TweetsPagerAdapter tweetsPagerAdapter;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.viewpager) ViewPager vpPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.fabCompose) FloatingActionButton fabCompose;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        vpPager.setAdapter(tweetsPagerAdapter);
        tabLayout.setupWithViewPager(vpPager);
        twitterClient = TwitterApp.getRestClient();
        context = getApplicationContext();
        setupProfileImage();
        attachFABListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public void onTweetSelected(Tweet tweet) {
        Intent i = new Intent(TimelineActivity.this, DetailActivity.class);
        i.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(i);
    }

    @Override
    public void onProfileSelected(User user) {
        startProfileIntent(user);
    }


    private void setupProfileImage() {
        twitterClient.getUserInfo(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    User user = User.fromJSON(response);
                    Glide.with(context)
                            .load(user.profileImageUrl)
                            .centerCrop().crossFade()
                            .transform(new CircleTransform(context))
                            .into(ivProfileImage);
                    setProfileOnClick(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TWITTERCLIENT", errorResponse.toString());
               // throwable.printStackTrace();
            }
        });
    }

    private void setProfileOnClick(final User user) {
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startProfileIntent(user);
            }
        });
    }

    private void startProfileIntent(User user) {
        Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
        i.putExtra("user", Parcels.wrap(user));
        startActivity(i);
    }

    private void attachFABListener() {
        fabCompose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                composeTweet();
            }
        });
    }

    private void composeTweet() {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        i.putExtra("isReply", false);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            insertTweet(tweet);
            Snackbar.make(vpPager, R.string.finish_compose, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void insertTweet(Tweet tweet) {
        HomeTimelineFragment htfragment = (HomeTimelineFragment) tweetsPagerAdapter.getRegisteredFragment(0);
        htfragment.insertTweetAtTop(tweet);
        vpPager.setCurrentItem(0);
    }

}
