package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    private ActivityProfileBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        String screenName = getIntent().getStringExtra("screen_name");
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, userTimelineFragment);
        ft.commit();
        setupToolbar();
        populateProfile();
    }

    private void populateProfile() {
        client = TwitterApp.getRestClient();
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    User user = User.fromJSON(response);
                    populateUserHeadline(user);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void populateUserHeadline(User user) {
        binding.tvName.setText(user.name);
        binding.tvTagline.setText(user.tagline);
        binding.tvFollowers.setText(user.followersCount + " Followers");
        binding.tvFollowing.setText(user.followingCount + " Following");
        Glide.with(this)
                .load(user.profileImageUrl)
                .centerCrop().crossFade()
                .transform(new CircleTransform(context))
                .into(binding.ivProfileImage);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.tbProfile);
        getSupportActionBar().setElevation(10);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
