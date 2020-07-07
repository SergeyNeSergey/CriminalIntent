package com.bignerdranch.android.criminalintent.database;

import android.app.Application;

import androidx.room.Room;


public class CrimeBaseHelper extends Application {
    public static CrimeBaseHelper instance;

    private CrimeLabData database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, CrimeLabData.class, "crime_data_base")
                .build();
    }

    public static CrimeBaseHelper getInstance() {
        return instance;
    }

    public CrimeLabData getDatabase() {
        return database;
    }
}
