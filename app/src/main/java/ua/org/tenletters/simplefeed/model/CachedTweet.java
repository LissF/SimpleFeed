package ua.org.tenletters.simplefeed.model;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.models.Tweet;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CachedTweet extends RealmObject {
    @PrimaryKey private Long id;
    private String cache;

    public void setTweet(final Tweet tweet) {
        id = tweet.getId();
        cache = new Gson().toJson(tweet);
    }

    public Tweet getTweet() {
        return new Gson().fromJson(cache, Tweet.class);
    }

    public Long getId() {
        return id;
    }
}
