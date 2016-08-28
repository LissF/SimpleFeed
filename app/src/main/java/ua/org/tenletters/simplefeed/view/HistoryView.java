package ua.org.tenletters.simplefeed.view;

import android.content.Context;
import android.widget.ListAdapter;

public interface HistoryView {
    Context getContext();

    void setHistoryAdapter(final ListAdapter adapter);

    void setRefreshing(final boolean refreshing);
}
