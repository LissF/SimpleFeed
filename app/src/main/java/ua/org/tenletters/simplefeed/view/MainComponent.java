package ua.org.tenletters.simplefeed.view;


import dagger.Component;
import ua.org.tenletters.simplefeed.app.dagger.ApplicationComponent;
import ua.org.tenletters.simplefeed.app.dagger.PerActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = MainModule.class)
interface MainComponent {
    void inject(FeedFragment fragment);

    void inject(HistoryFragment fragment);
}
