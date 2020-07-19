package com.bignerdranch.android.criminalintent;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.room.Room;


import com.bignerdranch.android.criminalintent.database.CrimeLabData;
import com.bignerdranch.android.criminalintent.database.CrimeLaboratory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CrimeLab implements Runnable {
    private static CrimeLab sCrimeLab;
    public CrimeLaboratory crimeLaboratory;
    private Context mContext;
    private CrimeLabData database;
    private Thread newThread;

    private UUID idOut;
    private Crime crimeOut;
    private Crime crimeIn;
    private List<Crime> listOut;


    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        database = Room.databaseBuilder(mContext, CrimeLabData.class, "crime_data_base")
                .build();
        crimeLaboratory = database.mCrimeLaboratory();


    }

    public void addCrime(Crime c) {
        newThread = new Thread(this, "addCrime");
        crimeIn=c;
        newThread.start();
        try{
            Thread.sleep(500);}
        catch (InterruptedException  e)
        { Log.e("CrimeLab","UI Thread was braked");}


    }

    public void deleteCrime(Crime c) {
        newThread = new Thread(this, "deleteCrime");
        crimeIn=c;
        newThread.start();
        try{
            Thread.sleep(500);}
        catch (InterruptedException  e)
        { Log.e("CrimeLab","UI Thread was braked");}


    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public List<Crime> getCrimes()  {
        newThread = new Thread(this, "getCrimes");
        newThread.start();
        try{
        Thread.sleep(500);}
        catch (InterruptedException  e)
        { Log.e("CrimeLab","UI Thread was braked");}
        return listOut;
    }

    public Crime getCrime(UUID id) {
        newThread = new Thread(this, "getCrime");
        idOut =id;
        newThread.start();
        try{
            Thread.sleep(500);}
        catch (InterruptedException  e)
        { Log.e("CrimeLab","UI Thread was braked");}
        return crimeOut;
    }

    public void updateCrime(Crime crime) {
        newThread = new Thread(this, "updateCrime");
        crimeIn=crime;
        newThread.start();
        try{
            Thread.sleep(500);}
        catch (InterruptedException  e)
        { Log.e("CrimeLab","UI Thread was braked");}

    }


    @Override
    public void run() {
        try {
            switch (newThread.getName()) {
                case "addCrime":
                    crimeLaboratory.insert(crimeIn);
                    break;
                case "deleteCrime":
                    crimeLaboratory.delete(crimeIn);
                    break;
                case "getCrimes": listOut=crimeLaboratory.getAll();

                    break;
                case "getCrime":
                    crimeOut =crimeLaboratory.getById(idOut.toString());
                case "updateCrime":
                    crimeLaboratory.update(crimeIn);
            }

        } catch (Exception e) {
            Log.e("CrimeLab","Exception in work with database");
        }
    }
    public File getPhotoFile(Crime crime) throws IOException {
        File filesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(filesDir, crime.getPhotoFilename());}

}
