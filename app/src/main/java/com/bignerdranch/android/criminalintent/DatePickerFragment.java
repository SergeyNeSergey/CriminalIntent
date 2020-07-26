package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

//Фрагмент представляющий собой всплывающий диалог в виде календаря для выбора даты преступления.
public class DatePickerFragment extends DialogFragment {
    //Ключ для Intent по которому передается дата объекта Crime для записи в БД.
    public static final String EXTRA_DATE =
            "com.bignerdranch.android.criminalintent.date";
    //Ключ для объекта класса Bundle
    private static final String ARG_DATE = "date";
    private DatePicker mDatePicker;

    // Метод вызываемый при обращении к фрагменту, получает на вход текущую дату которую использует
//по умолчению в момент создания и сохранеяет в объекте класса Bundle
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // Метод в котором создается диалоговое окно инициализируемое календарём и по завершинию вызывается
//метод sendResult(date) который с помощью интента посылает выбранную пользователем дату активности
//запустившей диалог.
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        assert date != null;
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);
        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);
        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year, month, day).
                                        getTime();
                                sendResult(date);
                            }
                        })
                .create();
    }

    //метод посылающий выбранную пользователем дату при завершении фрагмента с Activity.RESULT_OK
    private void sendResult(Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
