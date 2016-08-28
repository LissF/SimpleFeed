package ua.org.tenletters.simplefeed.view;

import android.content.Context;
import android.widget.ListAdapter;

public interface FeedView {
    Context getContext();

    void setFeedAdapter(ListAdapter adapter);

    void setRefreshing(boolean refreshing);
}
