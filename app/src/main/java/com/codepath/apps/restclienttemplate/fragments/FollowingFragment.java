package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by tessavoon on 10/7/17.
 */

public class FollowingFragment extends UsersListFragment {

    private TwitterClient client;

    public FollowingFragment() {
    }

    public static FollowingFragment newInstance(String screenName) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        fragment.setArguments(args);
        fragment.populateUsers(-1);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void populateUsers(long nextCursor) {
        client = TwitterApp.getRestClient();
        String screenName = getArguments().getString("screen_name");
        client.getFriends(screenName, nextCursor, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    addItems(response.getJSONArray("users"));
                    setNextCursor(response.getLong("next_cursor"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("populateusers", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}
