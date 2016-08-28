package ua.org.tenletters.simplefeed.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ua.org.tenletters.simplefeed.R;

// TODO: add BroadcastReceiver to refresh feed on Internet connection
public final class FeedFragment extends BaseFragment implements FeedView {

    private static final String TAG = "FeedFragment";

    @Inject FeedPresenter presenter;

    @BindView(R.id.feed) ListView feed;
    @BindView(R.id.updater) SwipeRefreshLayout updater;

    private Unbinder unbinder;

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

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.initialize();
    }

    private void initialize() {
        this.getComponent(MainComponent.class).inject(this);
        this.presenter.setView(this);
        this.presenter.initialize();

        updater.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        updater.setOnRefreshListener(() -> {
            if (presenter != null) {
                presenter.onRefresh();
            }
        });
    }

    @Override public void setFeedAdapter(final ListAdapter adapter) {
        if (feed != null) {
            feed.setAdapter(adapter);
        }
    }

    @Override public void setRefreshing(final boolean refreshing) {
        if (updater != null) {
            updater.setRefreshing(refreshing);
        }
    }
}
