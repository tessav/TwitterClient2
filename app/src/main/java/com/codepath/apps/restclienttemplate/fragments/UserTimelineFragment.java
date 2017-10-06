package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.PaginationParamType;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by tessavoon on 10/4/17.
 */

public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        userTimelineFragment.populateTimeline(PaginationParamType.SINCE, 1);
        return userTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void populateTimeline(PaginationParamType tweetIdType, long tweetId) {
        client = TwitterApp.getRestClient();
        String screenName = getArguments().getString("screen_name");
        client.getUserTimeline(tweetIdType, tweetId, screenName, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TWITTERCLIENT", response.toString());
                addItems(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TWITTERCLIENT", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}
