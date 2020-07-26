package com.bignerdranch.android.criminalintent;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

//Фрагмент представляющий собой всплывающий диалог в виде часов для выбора даты преступления
public class TimePickerFragment extends DialogFragment {
    //Ключ для Intent по которому передается время объекта Crime для записи в БД.
    public static final String EXTRA_TIME =
            "com.bignerdranch.android.criminalintent.time";
    //Ключ для объекта класса Bundle
    private static final String ARG_TIME = "time";
    private TimePicker mTimePicker;

    // Метод вызываемый при обращении к фрагменту, получает на вход текущую дату которую использует
//по умолчению в момент создания и сохранеяет в объекте класса Bundle
    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // Метод в котором создается диалоговое окно инициализируемое часами и по завершинию вызывается
//метод sendResult(date) который с помощью интента посылает выбранную пользователем дату активности
//запустившей диалог.
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        assert date != null;
        calendar.setTime(date);
        int time = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);
        mTimePicker = v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentHour(time);
        mTimePicker.setCurrentMinute(min);
        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour = mTimePicker.getCurrentHour();
                                int min = mTimePicker.getCurrentMinute();
                                Date date = new GregorianCalendar(0, 0, 0, hour, min).
                                        getTime();
                                sendResult(date);
                            }
                        })
                .create();
    }

    //метод посылающий выбранное пользователем время при завершении фрагмента с Activity.RESULT_OK
    private void sendResult(Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }


}
