package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDraft;
import com.codepath.apps.restclienttemplate.utils.PaginationParamType;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "8Yk98WZroNw5i73gKCXiSTd4t";       // Change this
	public static final String REST_CONSUMER_SECRET = "TkDkBXyAvilVs7VKL0WHO0Ngm7DgV319GY3Djzs63wUZRW9oYh"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	public void getHomeTimeline(PaginationParamType tweetIdType, long tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put(tweetIdType.param(), tweetId);
		params.put("count", 25);
		client.get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(PaginationParamType tweetIdType, long tweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put(tweetIdType.param(), tweetId);
		client.get(apiUrl, params, handler);
	}

	public void getUserTimeline(PaginationParamType tweetIdType, long tweetId, String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("screen_name", screenName);
		params.put(tweetIdType.param(), tweetId);
		client.get(apiUrl, params, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/account/verify_credentials.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	public void postTweet(TweetDraft draft, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", draft.postBody);
		if (draft.isReply) {
			params.put("in_reply_to_status_id", draft.statusId);
		}
		client.post(apiUrl, params, handler);
	}

	public void getFollowers(String screenName, long nextCursor, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/followers/list.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		params.put("next_cursor", nextCursor);
		client.get(apiUrl, params, handler);
	}

	public void getFriends(String screenName, long nextCursor, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/friends/list.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		params.put("cursor", nextCursor);
		client.get(apiUrl, params, handler);
	}

	public void favoriteTweet(Tweet tweet, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", tweet.uid);
		client.post(apiUrl, params, handler);
	}

	public void unfavoriteTweet(Tweet tweet, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", tweet.uid);
		client.post(apiUrl, params, handler);
	}

	public void retweet(Tweet tweet, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/retweet/" + tweet.uid + ".json");
		client.post(apiUrl, handler);
	}

	public void unretweet(Tweet tweet, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/unretweet/" + tweet.uid + ".json");
		client.post(apiUrl, handler);
	}

	public void lookupUser(String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/users/lookup.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		client.get(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
