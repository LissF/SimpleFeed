package ua.org.tenletters.simplefeed;

import android.support.v7.app.AppCompatActivity;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

import ua.org.tenletters.simplefeed.view.login.LoginActivity;
import ua.org.tenletters.simplefeed.view.home.MainActivity;

public final class SplashActivity extends AppCompatActivity {

    @Override protected void onResume() {
        super.onResume();

        proceed();
    }

    private void proceed() {
        final TwitterSession activeSession = Twitter.getSessionManager().getActiveSession();
        if (activeSession != null && activeSession.getUserId() > 0) {
            startActivity(MainActivity.getLaunchIntent(this));
        } else {
            startActivity(LoginActivity.getLaunchIntent(this));
        }

        finish();
    }
}
