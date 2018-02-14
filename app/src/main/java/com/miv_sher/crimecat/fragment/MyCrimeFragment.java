package com.miv_sher.crimecat.fragment;

import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyCrimeFragment extends CrimeListFragment {

    public  static final int CRIME_ID = 1;
    public Button edit;

    public MyCrimeFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-posts")
                .child(getUid());

    }
}
