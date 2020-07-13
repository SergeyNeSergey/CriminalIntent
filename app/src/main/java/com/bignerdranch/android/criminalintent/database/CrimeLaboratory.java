package com.bignerdranch.android.criminalintent.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bignerdranch.android.criminalintent.Crime;


import java.util.List;

@Dao
public interface CrimeLaboratory {
    @Query("SELECT * FROM Crime ")
    List<Crime> getAll();
    @Query("SELECT * FROM crime WHERE mId = :id")
    Crime getById(String id);
    @Query("SELECT COUNT(*) FROM Crime  ")
    Long getCount();
    @Insert
    void insert(Crime crime);

    @Update
    void update(Crime crime);

    @Delete
    void delete(Crime crime);

    @Query("UPDATE crime SET mTitle = :newTitle WHERE mid IN (:idList)")
    void updateTitleByIdList(List<Long> idList, String newTitle);

    @Query("UPDATE crime SET mDate = :newDate WHERE mid IN (:idList)")
    void updateDateByIdList(List<Long> idList, Long newDate);

    @Query("UPDATE crime SET mSolved = :newSolved WHERE mid IN (:idList)")
    void updateSolvedByIdList(List<Long> idList, boolean newSolved);

    @Query("UPDATE crime SET mRequiresPolice= :newRequiresPolice WHERE mid IN (:idList)")
    void updatePoliceByIdList(List<Long> idList, boolean newRequiresPolice);
}
