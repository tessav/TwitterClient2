package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by tessavoon on 9/27/17.
 */

@Parcel
public class User {

    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;
    public String tagline;
    public int followersCount;
    public int followingCount;
    public boolean verified;

    public User() {}

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");
        user.verified = jsonObject.getBoolean("verified");
        user.tagline = jsonObject.getString("description");
        user.followersCount = jsonObject.getInt("followers_count");
        user.followingCount = jsonObject.getInt("friends_count");
        return user;
    }
}
