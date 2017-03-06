package ua.org.tenletters.simplefeed.view.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import ua.org.tenletters.simplefeed.R;
import ua.org.tenletters.simplefeed.Utils;
import ua.org.tenletters.simplefeed.view.BaseActivity;
import ua.org.tenletters.simplefeed.view.home.MainActivity;

public final class LoginActivity extends BaseActivity {

    @BindView(R.id.twitter_login_button) TwitterLoginButton loginButton;

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(R.layout.activity_login);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> result) {
                startActivity(MainActivity.getLaunchIntent(LoginActivity.this));
            }

            @Override
            public void failure(final TwitterException exception) {
                Utils.logE("LoginActivity", "Login with Twitter failed!", exception);
            }
        });
    }

    @Override protected void onActivityResult(final int requestCode, final int resultCode,
                                              final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
