package ua.org.tenletters.simplefeed.view;


import dagger.Module;
import dagger.Provides;
import ua.org.tenletters.simplefeed.app.dagger.PerActivity;

@Module
final class MainModule {

    private final MainActivity activity;

    public MainModule(MainActivity activity) {
        this.activity = activity;
    }

    @Provides @PerActivity MainActivity provideMainActivity() {
        return this.activity;
    }
}