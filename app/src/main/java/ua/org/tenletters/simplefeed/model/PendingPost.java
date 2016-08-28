package ua.org.tenletters.simplefeed.model;

import android.content.Intent;

import com.google.gson.Gson;

import io.realm.RealmObject;

public class PendingPost extends RealmObject {
    private String cache;

    public void setIntent(final Intent intent) {
        cache = new Gson().toJson(intent);
    }

    public Intent getIntent() {
        return new Gson().fromJson(cache, Intent.class);
    }

}