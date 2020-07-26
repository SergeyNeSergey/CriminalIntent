package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

// Фрагмент представляющий собой всплывающее окно для просмотра увеличенной версии фотографии.
public class PhotoFragment extends DialogFragment {
    //Ключ для получения пути к фото сохраненном в объекте класса Bundle
    private static final String ARG_PHOTO = "photo";

    // Метод вызываемый при обращении к фрагменту, получает на вход местоположение фотофайла и
    //сохраняет его в объекте класса Bundle.
    public static PhotoFragment newInstance(String uri) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, uri);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //Метод создающий диалоговое окно с увеличенной версией фотографии выбранной пользователем
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        String uri = (String) getArguments().getSerializable(ARG_PHOTO);
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_photo, null);
        ImageView photoView = (ImageView) v.findViewById(R.id.crime_photo_full_screen);
        photoView.setImageURI(Uri.parse(uri));
        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(v).create();

    }
}
