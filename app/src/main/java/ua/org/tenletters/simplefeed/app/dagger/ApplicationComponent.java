package ua.org.tenletters.simplefeed.app.dagger;

import javax.inject.Singleton;

import dagger.Component;
import ua.org.tenletters.simplefeed.app.App;
import ua.org.tenletters.simplefeed.view.BaseActivity;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent extends AppDependencies {
    void inject(App app);

    void inject(BaseActivity baseActivity);
}
