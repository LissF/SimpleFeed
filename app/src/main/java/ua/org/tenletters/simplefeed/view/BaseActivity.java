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

    void init(@LayoutRes final int layout) {
        setContentView(layout);
        ButterKnife.bind(this);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
    }

    ApplicationComponent getApplicationComponent() {
        return App.getApplicationComponent(this);
    }
}
