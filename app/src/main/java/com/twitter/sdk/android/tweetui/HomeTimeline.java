package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit2.Call;

public class HomeTimeline extends BaseTimeline implements Timeline<Tweet> {

    private static final String SCRIBE_SECTION = "home";

    private final Integer maxItemsPerRequest;
    private final Boolean includeReplies;
    private final Boolean includeEntities;

    private HomeTimeline(Integer maxItemsPerRequest, Boolean includeReplies, Boolean includeEntities) {
        this.maxItemsPerRequest = maxItemsPerRequest;
        this.includeReplies = includeReplies != null && includeReplies;
        this.includeEntities = includeEntities;
    }

    public void next(Long sinceId, Callback<TimelineResult<Tweet>> cb) {
        createHomeTimelineRequest(sinceId, null).enqueue(new TweetsCallback(cb));
    }

    public void previous(Long maxId, Callback<TimelineResult<Tweet>> cb) {
        createHomeTimelineRequest(null, decrementMaxId(maxId)).enqueue(new TweetsCallback(cb));
    }

    String getTimelineType() {
        return SCRIBE_SECTION;
    }

    private Call<List<Tweet>> createHomeTimelineRequest(final Long sinceId, final Long maxId) {
        return TwitterCore.getInstance().getApiClient().getStatusesService().homeTimeline(maxItemsPerRequest,
                sinceId, maxId,
                false, includeReplies,
                null, includeEntities);
    }

    public static class Builder {

        private Integer maxItemsPerRequest;
        private Boolean includeReplies;
        private Boolean includeEntities;

        public Builder() {
            this.maxItemsPerRequest = 30;
        }

        public HomeTimeline.Builder maxItemsPerRequest(Integer maxItemsPerRequest) {
            this.maxItemsPerRequest = maxItemsPerRequest;
            return this;
        }

        public HomeTimeline.Builder includeReplies(Boolean includeReplies) {
            this.includeReplies = includeReplies;
            return this;
        }

        public HomeTimeline.Builder includeEntities(Boolean includeEntities) {
            this.includeEntities = includeEntities;
            return this;
        }

        public HomeTimeline build() {
            return new HomeTimeline(this.maxItemsPerRequest, this.includeReplies, this.includeEntities);
        }
    }
}