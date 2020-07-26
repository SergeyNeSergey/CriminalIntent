package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class PhotoFragment extends DialogFragment {
    private static final String ARG_PHOTO = "photo";

    private ImageView mPhotoView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String uri = (String) getArguments().getSerializable(ARG_PHOTO);
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_photo, null);
        mPhotoView=(ImageView) v.findViewById(R.id.crime_photo_full_screen);
        mPhotoView.setImageURI(Uri.parse(uri));
        return new AlertDialog.Builder(getActivity())
                .setView(v).create();

    }
    public static PhotoFragment newInstance(String uri) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, uri);
        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
}
}
