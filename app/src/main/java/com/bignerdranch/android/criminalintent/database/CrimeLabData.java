package com.bignerdranch.android.criminalintent.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bignerdranch.android.criminalintent.Crime;

@Database(entities = {Crime.class}, version = 1)
public abstract class CrimeLabData extends RoomDatabase {
    public abstract CrimeLaboratory mCrimeLaboratory();
}
