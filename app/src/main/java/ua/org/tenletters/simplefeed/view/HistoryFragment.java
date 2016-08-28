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

public final class HistoryFragment extends BaseFragment implements HistoryView {

    @Inject HistoryPresenter presenter;

    @BindView(R.id.history) ListView history;
    @BindView(R.id.updater) SwipeRefreshLayout updater;

    private Unbinder unbinder;

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

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        getComponent(MainComponent.class).inject(this);
        presenter.setView(this);
        presenter.initialize();

        updater.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        updater.setOnRefreshListener(() -> {
            if (presenter != null) {
                presenter.onRefresh();
            }
        });
    }

    @Override public void setHistoryAdapter(final ListAdapter adapter) {
        if (history != null) {
            history.setAdapter(adapter);
        }
    }

    @Override public void setRefreshing(final boolean refreshing) {
        if (updater != null) {
            updater.setRefreshing(refreshing);
        }
    }
}
