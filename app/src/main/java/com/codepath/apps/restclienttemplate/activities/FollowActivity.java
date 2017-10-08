package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityFollowBinding;
import com.codepath.apps.restclienttemplate.fragments.FollowerFragment;
import com.codepath.apps.restclienttemplate.fragments.FollowingFragment;
import com.codepath.apps.restclienttemplate.fragments.UsersListFragment;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

public class FollowActivity extends AppCompatActivity implements UsersListFragment.UserSelectedListener {

    private ActivityFollowBinding binding;
    Context context;
    User user;
    String followType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        context = getApplicationContext();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow);
        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        followType = getIntent().getStringExtra("follow_type");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (followType.equals("following")) {
            binding.toolbarTitle.setText("Following");
            FollowingFragment followingFragment = FollowingFragment.newInstance(user.screenName);
            ft.replace(R.id.flContainer, followingFragment);
        } else {
            binding.toolbarTitle.setText("Followers");
            FollowerFragment followerFragment = FollowerFragment.newInstance(user.screenName);
            ft.replace(R.id.flContainer, followerFragment);
        }
        ft.commit();
        setupToolbar();
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
    public void onUserSelected(User user) {
        // do nothing
    }

    @Override
    public void onProfileSelected(User user) {
        // TODO: follow or unfollow
    }
}
