package ua.org.tenletters.simplefeed.view;

import android.support.annotation.NonNull;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.HomeTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ua.org.tenletters.simplefeed.Utils;

public final class FeedPresenter implements Presenter {

    private static final String TAG = "FeedPresenter";

    private FeedView view;

    private Realm realm;

    private TweetTimelineListAdapter adapter;

    @Inject FeedPresenter() {
        // Empty
    }

    public void setView(@NonNull final FeedView view) {
        this.view = view;
    }

    public void initialize() {
        final long userId = Twitter.getSessionManager().getActiveSession().getUserId();
        realm = Realm.getInstance(new RealmConfiguration.Builder(view.getContext())
                .name("feed" + userId)
                .deleteRealmIfMigrationNeeded()
                .build());

        adapter = new TweetTimelineListAdapter.Builder(view.getContext())
                .setTimeline(new CachedTimeline(view.getContext(),
                        new HomeTimeline.Builder().build(), realm))
                .build();

        view.setFeedAdapter(adapter);
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }

    @Override public void destroy() {
        realm.close();
    }

    public void onRefresh() {
        adapter.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override public void success(final Result<TimelineResult<Tweet>> result) {
                if (view != null) {
                    Utils.logD(TAG, "Refreshed");
                    view.stopRefreshing();
                }
            }

            @Override public void failure(final TwitterException exception) {
                if (view != null) {
                    Utils.logD(TAG, "Failed to refresh");
                    view.stopRefreshing();
                }
            }
        });
    }
}
