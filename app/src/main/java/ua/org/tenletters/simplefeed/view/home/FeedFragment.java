package ua.org.tenletters.simplefeed.view.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.HomeTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ua.org.tenletters.simplefeed.R;
import ua.org.tenletters.simplefeed.Utils;
import ua.org.tenletters.simplefeed.view.BaseFragment;

public final class FeedFragment extends BaseFragment {

    private static final String TAG = "FeedFragment";

    @BindView(R.id.feed) ListView feed;
    @BindView(R.id.updater) SwipeRefreshLayout updater;

    private Unbinder unbinder;

    private Realm realm;

    private TweetTimelineListAdapter adapter;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(final Context context, final Intent intent) {
            Utils.logD(TAG, "Connectivity changed");
            if (Utils.isInternetAvailable(context)) {
                refresh();
            }
        }
    };

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                       final Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.initialize();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        realm.close();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        final Context context = getContext();
        if (context != null) {
            try {
                context.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                Utils.logE(TAG, "Can not unregister connectivity change receiver!", e);
            }
        }
    }

    private void initialize() {
        initFeed();

        updater.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        updater.setOnRefreshListener(this::refresh);

        getContext().registerReceiver(receiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), null, null);
    }

    private void initFeed() {
        final long userId = Twitter.getSessionManager().getActiveSession().getUserId();
        realm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("feed" + userId)
                .deleteRealmIfMigrationNeeded()
                .build());

        final Context context = getContext();
        adapter = new TweetTimelineListAdapter.Builder(context)
                .setTimeline(new CachedTimeline(context, new HomeTimeline.Builder().build(), realm))
                .build();

        if (feed != null) {
            feed.setAdapter(adapter);
        }
    }

    private void refresh() {
        adapter.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override public void success(final Result<TimelineResult<Tweet>> result) {
                Utils.logD(TAG, "Refreshed");
                stopRefreshing();
            }

            @Override public void failure(final TwitterException exception) {
                Utils.logD(TAG, "Failed to refresh");
                stopRefreshing();
            }
        });
    }

    private void stopRefreshing() {
        if (updater != null) {
            updater.setRefreshing(false);
        }
    }
}
