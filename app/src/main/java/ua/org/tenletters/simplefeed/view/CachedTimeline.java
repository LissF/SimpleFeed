package ua.org.tenletters.simplefeed.view;

import android.content.Context;
import android.os.Handler;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineCursor;
import com.twitter.sdk.android.tweetui.TimelineResult;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import ua.org.tenletters.simplefeed.Utils;
import ua.org.tenletters.simplefeed.model.CachedTweet;

public final class CachedTimeline implements Timeline<Tweet> {

    private static final String TAG = "CachedTimeline";

    private static final int LIMIT = 50;

    private Context context;
    private final Timeline<Tweet> timeline;
    private Realm realm;
    private Handler mainHandler;

    public CachedTimeline(final Context context, final Timeline<Tweet> timeline, final Realm realm) {
        this.context = context;
        this.timeline = timeline;
        this.realm = realm;
        this.mainHandler = new Handler(context.getMainLooper());
    }

    @Override public void next(final Long minPosition, final Callback<TimelineResult<Tweet>> cb) {
        if (Utils.isInternetAvailable(context)) {
            timeline.next(minPosition, new CachingCallback(cb));
        } else {
            realm.executeTransactionAsync(realm -> {
                final List<CachedTweet> cachedTweets = new LinkedList<>();
                final RealmResults<CachedTweet> results = getNextCachedTweets(realm, minPosition);
                if (results != null && !results.isEmpty()) {
                    cachedTweets.addAll(results.subList(0, Math.min(LIMIT, results.size())));
                }
                returnCachedDataToCallback(cachedTweets, cb);
            });
        }
    }

    @Override
    public void previous(final Long maxPosition, final Callback<TimelineResult<Tweet>> cb) {
        if (Utils.isInternetAvailable(context)) {
            timeline.previous(maxPosition, new CachingCallback(cb));
        } else {
            realm.executeTransactionAsync(realm -> {
                final List<CachedTweet> cachedTweets = new LinkedList<>();
                final RealmResults<CachedTweet> results = getPreviousCachedTweets(realm, maxPosition);
                if (results != null && !results.isEmpty()) {
                    cachedTweets.addAll(results.subList(0, Math.min(LIMIT, results.size())));
                }
                returnCachedDataToCallback(cachedTweets, cb);
            });
        }
    }

    private RealmResults<CachedTweet> getNextCachedTweets(final Realm realm,
                                                          final Long minPosition) {
        final RealmQuery<CachedTweet> query = realm.where(CachedTweet.class);
        return minPosition == null
               ? query.findAllSorted("id", Sort.DESCENDING)
               : query.greaterThan("id", minPosition)
                       .findAllSorted("id", Sort.ASCENDING);
    }

    private RealmResults<CachedTweet> getPreviousCachedTweets(final Realm realm,
                                                              final Long maxPosition) {
        return realm.where(CachedTweet.class)
                .lessThan("id", maxPosition)
                .findAllSorted("id", Sort.DESCENDING);
    }

    private void returnCachedDataToCallback(final List<CachedTweet> cachedTweets,
                                            final Callback<TimelineResult<Tweet>> cb) {
        final List<Tweet> tweets = new LinkedList<>();
        for (CachedTweet cachedTweet : cachedTweets) {
            tweets.add(cachedTweet.getTweet());
        }

        Utils.logD(TAG, "Got tweets from cache: " + tweets.size());

        if (tweets.isEmpty()) {
            mainHandler.post(() -> cb.failure(new TwitterException("Can not get tweets from cache!")));
        } else {
            final long firstId = tweets.get(0).getId();
            final long lastId = tweets.get(tweets.size() - 1).getId();
            final TimelineResult<Tweet> timelineResult = new TimelineResult<>(
                    new TimelineCursor(Math.min(firstId, lastId),
                            Math.max(firstId, lastId)), tweets);
            mainHandler.post(() -> cb.success(new Result<>(timelineResult, null)));
        }
    }

    private final class CachingCallback extends Callback<TimelineResult<Tweet>> {
        private final Callback<TimelineResult<Tweet>> cb;

        public CachingCallback(final Callback<TimelineResult<Tweet>> cb) {
            this.cb = cb;
        }

        @Override public void success(final Result<TimelineResult<Tweet>> result) {
            cacheTweets(result.data.items);
            cb.success(result);
        }

        @Override public void failure(final TwitterException exception) {
            Utils.logE(TAG, "Can not download tweets!", exception);
            cb.failure(exception);
        }

        private void cacheTweets(final List<Tweet> tweets) {
            realm.executeTransactionAsync(realm -> {
                final List<CachedTweet> cachedTweets = new LinkedList<>();
                for (Tweet tweet : tweets) {
                    final CachedTweet cachedTweet = new CachedTweet();
                    cachedTweet.setTweet(tweet);
                    cachedTweets.add(cachedTweet);
                }
                realm.copyToRealmOrUpdate(cachedTweets);
            });
        }
    }
}
