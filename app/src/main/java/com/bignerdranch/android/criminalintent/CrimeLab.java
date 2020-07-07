package com.bignerdranch.android.criminalintent;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;


import com.bignerdranch.android.criminalintent.database.CrimeLaboratory;

import java.util.List;
import java.util.UUID;

public class CrimeLab extends AppCompatActivity implements Runnable {
    private static CrimeLab sCrimeLab;
    public CrimeLaboratory CrimeLaboratory;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();



    }
    public void addCrime(Crime c) {
        run();
        CrimeLaboratory.insert(c);
    }
    public void deleteCrime(Crime c) {
        run();
        CrimeLaboratory.delete(c);

    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes() {
        run();
        return CrimeLaboratory.getAll();
    }

    public Crime getCrime(UUID id) {
        run();
            return CrimeLaboratory.getById(id.toString());
    }
    public void updateCrime(Crime crime) {
        run();
        CrimeLaboratory.update(crime);

    }



    @Override
    public void run() {

    }
}
