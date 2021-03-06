package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.NetworkUtils;
import com.codepath.apps.restclienttemplate.utils.PaginationParamType;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by tessavoon on 10/4/17.
 */

public abstract class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    SwipeRefreshLayout swipeContainer;
    LinearLayoutManager linearLayoutManager;
    TwitterClient client;
    private EndlessRecyclerViewScrollListener scrollListener;

    public interface TweetSelectedListener {
        public void onTweetSelected(Tweet tweet);
        public void onProfileSelected(User user);
        public void onReplySelected(Tweet tweet);
        public void onScreenNameSelected(String text);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets, this);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweet);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
        client = TwitterApp.getRestClient();
        addDividers();
        attachScrollListener();
        attachPullToRefreshListener();
        return v;
    }

    private void addDividers() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                linearLayoutManager.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);
    }

    public void addItems(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected(tweet);
    }

    @Override
    public void onProfileSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onProfileSelected(tweet.user);
    }

    @Override
    public void onReplySelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onReplySelected(tweet);
    }

    @Override
    public void onScreenNameSelected(String text) {
        ((TweetSelectedListener) getActivity()).onScreenNameSelected(text);
    }

    @Override
    public void onFavoriteSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        if (tweet.isFavorite) {
            unfavorite(tweet);
        } else {
            favorite(tweet);
        }
    }

    @Override
    public void onRetweetSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        if (tweet.isRetweet) {
            unretweet(tweet);
        } else {
            retweet(tweet);
        }
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

    private void attachScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Tweet maxTweet = tweets.get(totalItemsCount - 1);
                checkAndPopulate(PaginationParamType.MAX, maxTweet.uid - 1);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
    }

    private void attachPullToRefreshListener() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweetAdapter.clear();
                checkAndPopulate(PaginationParamType.SINCE, 1);
                swipeContainer.setRefreshing(false);
            }
        });

    }

    private void checkAndPopulate(PaginationParamType tweetIdType, long tweetId) {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            populateTimeline(tweetIdType, tweetId);
        } else {
            Snackbar.make(rvTweets, R.string.not_connected, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    protected abstract void populateTimeline(PaginationParamType tweetIdType, long tweetId);
}
