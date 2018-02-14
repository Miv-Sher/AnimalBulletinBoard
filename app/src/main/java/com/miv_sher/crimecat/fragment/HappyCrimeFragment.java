package com.miv_sher.crimecat.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class HappyCrimeFragment extends CrimeListFragment {

    public  static final int CRIME_ID = 1;

    public HappyCrimeFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        //return databaseReference.child("posts").orderByChild("starCount");
        return databaseReference.child("posts").orderByChild("happy")
                .startAt("true")
                .endAt("true");
    }
}
