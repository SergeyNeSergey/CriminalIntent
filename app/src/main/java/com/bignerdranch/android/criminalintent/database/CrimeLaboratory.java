package com.bignerdranch.android.criminalintent.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bignerdranch.android.criminalintent.Crime;

import java.util.List;

// Класс для запросов к базе данных.
@Dao
public interface CrimeLaboratory {
    @Query("SELECT * FROM Crime ")
    List<Crime> getAll();

    @Query("SELECT * FROM crime WHERE mId = :id")
    Crime getById(String id);

    @Insert
    void insert(Crime crime);

    @Update
    void update(Crime crime);

    @Delete
    void delete(Crime crime);


}
