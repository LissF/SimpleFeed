package ua.org.tenletters.simplefeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ua.org.tenletters.simplefeed.model.PendingPost;

public final class PostFailureReceiver extends BroadcastReceiver {
    private static final String TAG = "PostFailureReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TweetUploadService.UPLOAD_FAILURE.equals(intent.getAction())) {
            final Intent retryIntent =
                    intent.getParcelableExtra(TweetUploadService.EXTRA_RETRY_INTENT);

            final Realm realm = Realm.getInstance(new RealmConfiguration.Builder(context)
                    .name("pending")
                    .deleteRealmIfMigrationNeeded()
                    .build());

            realm.executeTransactionAsync(rlm -> {
                final PendingPost pendingPost = new PendingPost();
                final TwitterSession session = Twitter.getSessionManager().getActiveSession();
                if (session != null) {
                    pendingPost.setUserId(session.getUserId());
                }
                pendingPost.setIntent(retryIntent);
                rlm.copyToRealm(pendingPost);
            }, realm::close, error -> {
                Utils.logE(TAG, "Can not save intent!", error);
                realm.close();
            });
        }
    }
}