package com.bignerdranch.android.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Date mTime;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
        mTime = mDate;

    }
    public Crime() {
        this(UUID.randomUUID());
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    public UUID getId() {
        return mId;
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
    public Date getDate(){ return mDate;}

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getTimeHumanReadable() {
        Date time =mTime;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH: mm: ss zz", Locale.ENGLISH);
        return timeFormat.format(time);
    }
    public Date getTime() {return mTime;}

    public void setTime(Date time) {

        mTime = time;
    }




}
