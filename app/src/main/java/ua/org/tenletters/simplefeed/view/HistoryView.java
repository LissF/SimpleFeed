package ua.org.tenletters.simplefeed.view;

import android.content.Context;
import android.widget.ListAdapter;

interface HistoryView {
    Context getContext();

    void setHistoryAdapter(final ListAdapter adapter);

    void stopRefreshing();
}
