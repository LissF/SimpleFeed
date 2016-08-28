package ua.org.tenletters.simplefeed.view;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import ua.org.tenletters.simplefeed.app.App;
import ua.org.tenletters.simplefeed.app.dagger.ApplicationComponent;

public abstract class BaseActivity extends AppCompatActivity {

    protected void init(@LayoutRes final int layout) {
        setContentView(layout);
        ButterKnife.bind(this);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
    }

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void addFragment(@IdRes final int containerViewId, final Fragment fragment) {
        if (!isFinishing()) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(containerViewId, fragment)
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    /**
     * Replaces current {@link Fragment} in this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added instead of current one.
     */
    protected void replaceFragment(@IdRes final int containerViewId, final Fragment fragment) {
        if (!isFinishing()) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(containerViewId, fragment)
                    .commitNowAllowingStateLoss();
        }
    }

    protected ApplicationComponent getApplicationComponent() {
        return App.getApplicationComponent(this);
    }
}
