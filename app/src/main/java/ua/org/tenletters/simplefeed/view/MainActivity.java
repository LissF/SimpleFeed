package ua.org.tenletters.simplefeed.view;

import android.content.Context;
import android.content.Intent;
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

import butterknife.BindView;
import butterknife.OnClick;
import ua.org.tenletters.simplefeed.R;
import ua.org.tenletters.simplefeed.app.dagger.HasComponent;

public final class MainActivity extends BaseActivity implements HasComponent<MainComponent> {

    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.container) ViewPager viewPager;
    @BindView(R.id.fab) FloatingActionButton fab;

    private MainComponent mainComponent;

    private volatile boolean userWantsToExit = false;

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(R.layout.activity_main);
        initInjector();

        setSupportActionBar(toolbar);

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        setTitle("@" + Twitter.getSessionManager().getActiveSession().getUserName());
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
        // TODO: compose tweet
        Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_LONG).show();
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
