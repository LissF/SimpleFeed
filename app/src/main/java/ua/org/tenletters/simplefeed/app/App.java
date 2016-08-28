package ua.org.tenletters.simplefeed.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

import ua.org.tenletters.simplefeed.app.dagger.ApplicationComponent;
import ua.org.tenletters.simplefeed.app.dagger.ApplicationModule;
import ua.org.tenletters.simplefeed.app.dagger.DaggerApplicationComponent;

public final class App extends Application {

    private static final String TWITTER_KEY = "xnOfzYzg2cZuZbRjbeoF5i02Z";
    private static final String TWITTER_SECRET = "fbEeFE6DL00smbaRKJS2fRSDJu32Wk6HwW4yCkrQdReaRWngYU";

    private ApplicationComponent applicationComponent;

    public static ApplicationComponent getApplicationComponent(@NonNull final Context context) {
        return ((App) context.getApplicationContext()).applicationComponent;
    }

    @Override public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetComposer());

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }
}
