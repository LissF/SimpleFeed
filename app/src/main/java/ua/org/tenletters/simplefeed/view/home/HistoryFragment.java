package ua.org.tenletters.simplefeed.view.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ua.org.tenletters.simplefeed.R;
import ua.org.tenletters.simplefeed.Utils;
import ua.org.tenletters.simplefeed.view.BaseFragment;

public final class HistoryFragment extends BaseFragment {

    private static final String TAG = "HistoryFragment";

    @BindView(R.id.history) ListView history;
    @BindView(R.id.updater) SwipeRefreshLayout updater;

    private Unbinder unbinder;

    private Realm realm;

    private TweetTimelineListAdapter adapter;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(final Context context, final Intent intent) {
            Utils.logD(TAG, "Tweet posted, refreshing history");
            if (Utils.isInternetAvailable(context)) {
                refresh();
            }
        }
    };

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                       final Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
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
        initHistory();

        updater.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        updater.setOnRefreshListener(this::refresh);

        getContext().registerReceiver(receiver,
                new IntentFilter(TweetUploadService.UPLOAD_SUCCESS), null, null);
    }

    private void initHistory() {
        final long userId = Twitter.getSessionManager().getActiveSession().getUserId();
        realm = Realm.getInstance(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("history" + userId)
                .build());

        final Context context = getContext();
        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(Twitter.getSessionManager().getActiveSession().getUserName())
                .build();
        adapter = new TweetTimelineListAdapter.Builder(context)
                .setTimeline(new CachedTimeline(context, userTimeline, realm))
                .build();

        if (history != null) {
            history.setAdapter(adapter);
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
