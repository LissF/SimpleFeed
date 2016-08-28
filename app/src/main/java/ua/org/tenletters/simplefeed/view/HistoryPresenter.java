package ua.org.tenletters.simplefeed.view;

import android.support.annotation.NonNull;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ua.org.tenletters.simplefeed.Utils;

public final class HistoryPresenter implements Presenter {

    private static final String TAG = "HistoryPresenter";

    private HistoryView view;

    private Realm realm;

    private TweetTimelineListAdapter adapter;

    @Inject HistoryPresenter() {
        // Empty
    }

    public void setView(@NonNull final HistoryView view) {
        this.view = view;
    }

    public void initialize() {
        final long userId = Twitter.getSessionManager().getActiveSession().getUserId();
        realm = Realm.getInstance(new RealmConfiguration.Builder(view.getContext())
                .deleteRealmIfMigrationNeeded()
                .name("history" + userId)
                .build());

        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(Twitter.getSessionManager().getActiveSession().getUserName())
                .build();
        adapter = new TweetTimelineListAdapter.Builder(view.getContext())
                .setTimeline(new CachedTimeline(view.getContext(), userTimeline, realm))
                .build();

        view.setHistoryAdapter(adapter);
    }

    @Override public void resume() {

    }

    @Override public void pause() {

    }

    @Override public void destroy() {
        realm.close();
    }

    void onRefresh() {
        adapter.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override public void success(final Result<TimelineResult<Tweet>> result) {
                if (view != null) {
                    Utils.logD(TAG, "Refreshed");
                    view.setRefreshing(false);
                }
            }

            @Override public void failure(final TwitterException exception) {
                if (view != null) {
                    Utils.logD(TAG, "Failed to refresh");
                    view.setRefreshing(false);
                }
            }
        });
    }
}