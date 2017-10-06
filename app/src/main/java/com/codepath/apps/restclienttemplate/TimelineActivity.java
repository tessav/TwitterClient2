package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    TwitterClient twitterClient;
    Context context;
    ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(), this));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
        twitterClient = TwitterApp.getRestClient();
        context = getApplicationContext();
        setupProfileImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public void onTweetSelected(Tweet tweet) {
         Toast.makeText(this, tweet.body, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProfileSelected(User user) {
        Toast.makeText(this, user.name, Toast.LENGTH_SHORT).show();
        startProfileIntent(user);
    }

    private void setupProfileImage() {
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
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
                throwable.printStackTrace();
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

}
