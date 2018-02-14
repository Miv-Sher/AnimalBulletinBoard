package com.miv_sher.crimecat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.miv_sher.crimecat.fragment.CrimeFragment;

import java.util.UUID;

public class CrimeActivity extends SingleActivity /*AppCompatActivity*/ {

    public static final String EXTRA_CRIME_ID =
            "com.miv_sher.crimecat.criminalintent.crime_id";

    public static final String EXTRA_POST_KEY = "post_key";

    @Override
    protected Fragment createFragment() {
        // return new CrimeFragment();
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }

    public static Intent setIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }


}