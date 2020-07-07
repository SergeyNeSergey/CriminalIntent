package com.bignerdranch.android.criminalintent;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
@Entity
public class Crime  {
    @PrimaryKey @NonNull
    public String mId;
    private String mTitle;
    public Long mDate;
    public Long mTime;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public Crime(UUID id) {
        mId = id.toString();
        mDate = new Date().getTime();
        mTime = mDate;


    }
    public Crime()  {
        this(UUID.randomUUID());
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    public UUID getId() {
        return UUID.fromString(mId);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDateHumanReadable() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMM d y", Locale.ENGLISH);
        String date = dateFormat.format(mDate);

        return date;
    }

    public Date getDate(){ return Date.from(Instant.ofEpochMilli(mDate));}

    public void setDate(Date date) {
        mDate = date.getTime();
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getTimeHumanReadable() {
        Date time =Date.from(Instant.ofEpochMilli(mTime));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH: mm: ss zz", Locale.ENGLISH);
        return timeFormat.format(time);
    }
    @TypeConverter
    public Date getTime() {return Date.from(Instant.ofEpochMilli(mTime));}

    public void setTime(Date time) {

        mTime = time.getTime();
    }

}
