package ua.org.tenletters.simplefeed.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ua.org.tenletters.simplefeed.R;
import ua.org.tenletters.simplefeed.Utils;
import ua.org.tenletters.simplefeed.app.dagger.HasComponent;
import ua.org.tenletters.simplefeed.model.PendingPost;

public final class MainActivity extends BaseActivity implements HasComponent<MainComponent> {

    private static final String TAG = "MainActivity";

    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager viewPager;
    @BindView(R.id.fab) FloatingActionButton fab;

    private MainComponent mainComponent;

    private Realm realm;

    private BroadcastReceiver receiver;

    private volatile boolean userWantsToExit = false;

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(R.layout.activity_main);
        initInjector();

        realm = Realm.getInstance(new RealmConfiguration.Builder(this)
                .name("pending")
                .deleteRealmIfMigrationNeeded()
                .build());

        sendPendingPostsIfWeCan();

        setSupportActionBar(toolbar);

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        setTitle("@" + Twitter.getSessionManager().getActiveSession().getUserName());

        receiver = new BroadcastReceiver() {
            @Override public void onReceive(final Context context, final Intent intent) {
                Utils.logD(TAG, "Connectivity changed");
                sendPendingPostsIfWeCan();
            }
        };
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), null, null);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        realm.close();
    }

    @Override public void setTitle(CharSequence title) {
        if (this.title != null) {
            this.title.setText(title);
        }
    }

    @Override public void setTitle(int titleId) {
        if (this.title != null) {
            this.title.setText(titleId);
        }
    }

    @Override public MainComponent getComponent() {
        return mainComponent;
    }

    @Override public void onBackPressed() {
        if (userWantsToExit) {
            super.onBackPressed();
        } else {
            userWantsToExit = true;
            toolbar.postDelayed(() -> userWantsToExit = false, 5000);
            Toast.makeText(this, R.string.tap_to_close, Toast.LENGTH_SHORT).show();
        }
    }

    private void initInjector() {
        this.mainComponent = DaggerMainComponent.builder()
                .applicationComponent(getApplicationComponent())
                .mainModule(new MainModule(this))
                .build();
    }

    @OnClick(R.id.exit) public void onLockClicked() {
        Twitter.logOut();
        startActivity(LoginActivity.getLaunchIntent(this));
    }

    @OnClick(R.id.fab) public void onFabClicked() {
        final TwitterSession session = Twitter.getSessionManager().getActiveSession();
        final Intent intent = new ComposerActivity.Builder(this)
                .session(session)
                .createIntent();
        startActivity(intent);
    }

    private void sendPendingPostsIfWeCan() {
        if (Utils.isInternetAvailable(this)) {
            realm.executeTransactionAsync(rlm -> {
                final TwitterSession session = Twitter.getSessionManager().getActiveSession();
                if (session != null) {
                    final RealmResults<PendingPost> pendingPosts = rlm.where(PendingPost.class)
                            .equalTo("userId", session.getUserId())
                            .findAll();
                    for (PendingPost pendingPost : pendingPosts) {
                        Utils.logD(TAG, "Sending post...");
                        final Intent intent = pendingPost.getIntent(this);
                        startService(intent);
                    }
                    pendingPosts.deleteAllFromRealm();
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int[] tabTitles = {
                R.string.tab_feed,
                R.string.tab_history
        };

        public SectionsPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override public Fragment getItem(final int position) {
            switch (position) {
                case 0:
                    return FeedFragment.newInstance();
                case 1:
                    return HistoryFragment.newInstance();
                default:
                    throw new AssertionError("We only have 2 tabs here!");
            }
        }

        @Override public int getCount() {
            return tabTitles.length;
        }

        @Override public CharSequence getPageTitle(final int position) {
            return getString(tabTitles[position]);
        }
    }
}
