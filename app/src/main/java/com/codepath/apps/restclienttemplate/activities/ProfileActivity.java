package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.CircleTransform;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    Context context;
    User user;

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
