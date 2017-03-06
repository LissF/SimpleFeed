package ua.org.tenletters.simplefeed;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

public final class App extends Application {

    private static final String TWITTER_KEY = "xnOfzYzg2cZuZbRjbeoF5i02Z";
    private static final String TWITTER_SECRET = "fbEeFE6DL00smbaRKJS2fRSDJu32Wk6HwW4yCkrQdReaRWngYU";

    @Override public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetComposer());

        Realm.init(this);
    }
}
