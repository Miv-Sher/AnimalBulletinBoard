package com.miv_sher.crimecat.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;

    // Закрытый конструктор


    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();


    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
           /* if (crime.getId().equals(id)) {
                return crime;
            }*/
        }
        return null;
    }


    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }
}