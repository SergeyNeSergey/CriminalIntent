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

// Класс относящийся к Модели. Заполняет базу данных своими полями такими как ID преступления
// (является первичным ключом), описание преступления, подозреваемый, дата, время,
// раскрыто преступление или нет, необходимость вызвать полицию.
@Entity
public class Crime {
    @PrimaryKey
    @NonNull
    public String mId;
    public Long mDate;
    public Long mTime;
    private String mTitle;
    private String mSuspect;
    private boolean mSolved;
    private boolean mRequiresPolice;

    public Crime(UUID id) {
        mId = id.toString();
        mDate = new Date().getTime();
        mTime = mDate;
        mTitle = "emty";


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

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public Date getDate() {
        return Date.from(Instant.ofEpochMilli(mDate));
    }

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
        Date time = Date.from(Instant.ofEpochMilli(mTime));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH: mm: ss zz", Locale.ENGLISH);
        return timeFormat.format(time);
    }

    @TypeConverter
    public Date getTime() {
        return Date.from(Instant.ofEpochMilli(mTime));
    }

    public void setTime(Date time) {

        mTime = time.getTime();
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

}
