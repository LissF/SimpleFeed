package ua.org.tenletters.simplefeed.model;

import android.content.Context;
import android.content.Intent;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

import java.lang.reflect.Field;

import io.realm.RealmObject;
import ua.org.tenletters.simplefeed.Utils;

public class PendingPost extends RealmObject {
    private String text;
    private Long userId;

    public void setIntent(final Intent intent) {
        try {
            final Field extraTextField = TweetUploadService.class.getDeclaredField("EXTRA_TWEET_TEXT");
            extraTextField.setAccessible(true);
            final String extraText = (String) extraTextField.get(null);
            intent.setExtrasClassLoader(TweetUploadService.class.getClassLoader());
            setText(intent.getStringExtra(extraText));
        } catch (Exception e) {
            Utils.logE("PendingPost", "Can not get Tweet data to save!", e);
        }
    }

    public Intent getIntent(final Context context) {
        final Intent result = new Intent(context, TweetUploadService.class);
        try {
            final Field extraTextField = TweetUploadService.class.getDeclaredField("EXTRA_TWEET_TEXT");
            extraTextField.setAccessible(true);
            final String extraText = (String) extraTextField.get(null);
            result.putExtra(extraText, getText());

            final TwitterSession session = Twitter.getSessionManager().getActiveSession();
            if (session != null) {
                final Field extraTokenField = TweetUploadService.class.getDeclaredField("EXTRA_USER_TOKEN");
                extraTokenField.setAccessible(true);
                final String extraToken = (String) extraTokenField.get(null);

                result.putExtra(extraToken, session.getAuthToken());
            }
        } catch (Exception e) {
            Utils.logE("PendingPost", "Can not load Tweet data to send!", e);
        }
        return result;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }
}