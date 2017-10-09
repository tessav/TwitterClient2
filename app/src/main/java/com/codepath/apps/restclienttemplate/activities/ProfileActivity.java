package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    private ActivityProfileBinding binding;
    Context context;
    User user;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(user.screenName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, userTimelineFragment);
        ft.commit();
        setupToolbar();
        populateUserHeadline(user);
        setupFollowListeners();
    }

    public void populateUserHeadline(User user) {
        binding.toolbarTitle.setText(user.name);
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

    @Override
    public void onTweetSelected(Tweet tweet) {
        Intent i = new Intent(ProfileActivity.this, DetailActivity.class);
        i.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(i);
    }

    @Override
    public void onProfileSelected(User user) {
       // stays in same profile
    }

    @Override
    public void onScreenNameSelected(String text) {
    }

    @Override
    public void onReplySelected(Tweet tweet) {
        replyToTweet(tweet);
    }

    private void setupFollowListeners() {
        binding.tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followIntent("follower");
            }
        });

        binding.tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followIntent("following");
            }
        });
    }

    private void followIntent(String followType) {
        Intent i = new Intent(ProfileActivity.this, FollowActivity.class);
        i.putExtra("user", Parcels.wrap(user));
        i.putExtra("follow_type", followType);
        startActivity(i);
    }

    private void replyToTweet(Tweet tweet) {
        Intent i = new Intent(ProfileActivity.this, ComposeActivity.class);
        i.putExtra("isReply", true);
        i.putExtra("screenName", tweet.user.screenName);
        i.putExtra("statusId", tweet.uid);
        startActivityForResult(i, REQUEST_CODE);
    }

}
