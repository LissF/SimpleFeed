package ua.org.tenletters.simplefeed.view;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected void init(@LayoutRes final int layout) {
        setContentView(layout);
        ButterKnife.bind(this);
    }
}
