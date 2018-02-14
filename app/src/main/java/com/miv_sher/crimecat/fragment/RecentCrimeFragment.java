package com.miv_sher.crimecat.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentCrimeFragment extends CrimeListFragment {

    public RecentCrimeFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("posts").orderByChild("happy")
                .startAt("false")
                .endAt("false")
                .limitToFirst(100);
        // [END recent_posts_query]

        return recentPostsQuery;
    }
}
