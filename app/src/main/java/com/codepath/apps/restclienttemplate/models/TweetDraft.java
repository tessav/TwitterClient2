package com.codepath.apps.restclienttemplate.models;

import org.parceler.Parcel;

/**
 * Created by tessavoon on 10/7/17.
 */

@Parcel
public class TweetDraft {
    public String postBody;
    public boolean isReply;
    public String statusId;
    public String screenName;

    public TweetDraft() {

    }
}