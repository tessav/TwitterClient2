package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.PaginationParamType;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by tessavoon on 10/4/17.
 */

public abstract class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    public interface TweetSelectedListener {
        public void onTweetSelected(Tweet tweet);
        public void onProfileSelected(User user);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets, this);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweet);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
        addDividers();
        attachScrollListener();
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

    private void attachScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Tweet maxTweet = tweets.get(totalItemsCount - 1);
                populateTimeline(PaginationParamType.MAX, maxTweet.uid - 1);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
    }

    protected abstract void populateTimeline(PaginationParamType tweetIdType, long tweetId);
}
