package com.bignerdranch.android.criminalintent;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int  REQUEST_CODE_PERMISSION_READ_CONTACTS=3;
    private static final int REQUEST_TELEPHONE = 4;
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mCallToPolice;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallToSuspect;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
    }
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                this.getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = v.findViewById(R.id.crime_title);
         mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
// Здесь намеренно оставлено пустое место
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
// И здесь тоже
            }
        });
        mDateButton = v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager dateManager = getFragmentManager();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMM d y", Locale.ENGLISH);
                DatePickerFragment dialog = null;
                try {
                    dialog = DatePickerFragment
                            .newInstance(dateFormat.parse(mCrime.getDateHumanReadable()));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(dateManager, DIALOG_DATE);
            }
        });
        mTimeButton = v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager timeManager = getFragmentManager();

                TimePickerFragment dialog= null;

                    dialog =  TimePickerFragment
                            .newInstance(mCrime.getTime());



                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(timeManager, DIALOG_TIME);

            }
        });
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        mCallToPolice = v.findViewById(R.id.needed_to_call_the_police);
        mCallToPolice.setChecked(mCrime.isRequiresPolice());
        mCallToPolice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setRequiresPolice(isChecked);
            }
        });
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity()).setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport()).setType("text/plain").createChooserIntent();
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        mCallToSuspect = (Button) v.findViewById(R.id.call_to_suspect);
        if (mCrime.getSuspect()==null) mCallToSuspect.setEnabled(false);
        mCallToSuspect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int permissionStatus = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CONTACTS},
                            REQUEST_CODE_PERMISSION_READ_CONTACTS);
                }


            }
        });
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
            mCallToSuspect.setEnabled(true);
        }
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);


        return v;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    readContacts();
                } else {
                }
                return;
        }
    }

    private void readContacts() {
        Log.i("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN", mCrime.getSuspect());

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + mCrime.getSuspect() +"%'";
            String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor c = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection, selection, null, null);
            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String number = c.getString(0);
                final Intent call = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:"+number));
                Log.i("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN",number);
                startActivity(call);

            } finally {
                c.close();
            }
        } catch (NullPointerException e) {
            Toast t =new Toast(getActivity());
            t.setText("You must choice the suspect!");
            t.show();
        }

        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();


        }
        if (requestCode == REQUEST_TIME) {
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setTime(time);
            updateTime();
        }
       if (requestCode == REQUEST_CONTACT && data != null) {
        Uri contactUri = data.getData();
// Определение полей, значения которых должны быть
// возвращены запросом.
        String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
        };
// Выполнение запроса - contactUri здесь выполняет функции
// условия "where"
        Cursor c = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);
        try {
// Проверка получения результатов
            if (c.getCount() == 0) {
                return;
            }
// Извлечение первого столбца данных - имени подозреваемого.
            c.moveToFirst();
            String suspect = c.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
        } finally {
            c.close();
        }
    }

    }
    private void updateTime() {
        mTimeButton.setText(mCrime.getTimeHumanReadable());
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDateHumanReadable());
    }
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
}
