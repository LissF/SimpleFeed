package ua.org.tenletters.simplefeed.app.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class ApplicationModule {
    private final Context context;

    public ApplicationModule(final Context context) {
        this.context = context;
    }

    @Provides @Singleton Context provideApplicationContext() {
        return context.getApplicationContext();
    }
}
