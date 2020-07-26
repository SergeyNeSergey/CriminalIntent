package com.bignerdranch.android.criminalintent;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

//Класс-фрагмент хостом которого яв-ся CrimePagerActivity при работе с мониторами с меньшей стороной < 600dp
//реализует ViewPager2. При работе с мониторами с меньшей стороной более 600dp заполняет две трети
//CrimeListActivity
public class CrimeFragment extends Fragment {
    //Ключ для получения crimeId из сохраненных значений класса Bundle
    private static final String ARG_CRIME_ID = "crime_id";
    // Ключ для добавления экземпляра DatePickerFragment в FragmentManager и вывода его на
    //экран
    private static final String DIALOG_DATE = "DialogDate";
    // Ключ для добавления экземпляра TimePickerFragment в FragmentManager и вывода его на
    //экран
    private static final String DIALOG_TIME = "DialogTime";
    // Ключ для добавления экземпляра PhotoFragment в FragmentManager и вывода его на
    //экран
    private static final String ARG_PHOTO = "photo";
    //requestCode для Intent DatePickerFragment
    private static final int REQUEST_DATE = 0;
    //requestCode для Intent TimePickerFragment
    private static final int REQUEST_TIME = 1;
    //requestCode для неявного Intent обращения к приложению записной книги контактов.
    private static final int REQUEST_CONTACT = 2;
    //requestCode для неявного Intent обращения к приложению записной книги контактов. С разрешением
    //считать контакт
    private static final int REQUEST_CODE_PERMISSION_READ_CONTACTS = 3;
    //requestCode для неявного Intent обращения к приложению для работы с фотокамерой.
    private static final int REQUEST_PHOTO = 4;
    private Crime mCrime;
    private File mPhotoFile;
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
    private Callbacks mCallbacks;

    //Метод для создания CrimeFragment
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //В момент прикрепления фрагмента к активности хосту
// выполняю непроверяемое преобразование своей активности к CrimeFragment.Callbacks .
// Это означает, что активность-хост должна реализовать CrimeFragment.Callbacks.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    //В момент создания фрагмента запрашиваю экземпляр Crime из CrimeLab. Пытаюсь получить фотографию
// из CrimeLab. Прикрепляю меню.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        try {
            mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
        }

        setHasOptionsMenu(true);
    }

    // В методе создания отображения фрагмента инициализирую текстовые поля,кнопки,кнопки с изображениями,
    // чекбоксы, изображения фотографий и реализую логику их работы. А так же логику работы с
    //диалоговыми окнами
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
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
// Здесь намеренно оставлено пустое место
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
                assert dialog != null;
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                assert dateManager != null;
                dialog.show(dateManager, DIALOG_DATE);
            }
        });
        mTimeButton = v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager timeManager = getFragmentManager();

                TimePickerFragment dialog;


                dialog = TimePickerFragment
                        .newInstance(mCrime.getTime());


                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                assert timeManager != null;
                dialog.show(timeManager, DIALOG_TIME);

            }
        });
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });
        mCallToPolice = v.findViewById(R.id.needed_to_call_the_police);
        mCallToPolice.setChecked(mCrime.isRequiresPolice());
        mCallToPolice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setRequiresPolice(isChecked);
                updateCrime();
            }
        });
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(Objects.requireNonNull(getActivity())).setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport()).setType("text/plain").createChooserIntent();
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        mCallToSuspect = (Button) v.findViewById(R.id.call_to_suspect);
        mCallToSuspect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int permissionStatus = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_CONTACTS);
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
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
        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
                        "com.bignerdranch.android.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager photoManager = getFragmentManager();

                PhotoFragment dialog;

                dialog = PhotoFragment.newInstance(mPhotoFile.getPath());


                dialog.setTargetFragment(CrimeFragment.this, 6);
                assert photoManager != null;
                dialog.show(photoManager, ARG_PHOTO);

            }
        });


        return v;
    }

    // Обновляю преступления в момент паузы.
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    //В момент открепления фрагмента обнуляю mCallbacks
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    //Создаю меню
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);

    }

    // При нажатии на кнопку "удаления" удаляю объект Crime
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                Objects.requireNonNull(this.getActivity()).finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Проверяю разрешение на чтение контактов, если оно получено, то вызываю метод чтения контактов.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                }
        }
    }

    //Обрабатываю результаты интентов.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            assert date != null;
            mCrime.setDate(date);
            updateCrime();
            updateDate();


        }
        if (requestCode == REQUEST_TIME) {
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            assert time != null;
            mCrime.setTime(time);
            updateCrime();
            updateTime();
        }
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            assert contactUri != null;
            try (Cursor c = Objects.requireNonNull(getActivity()).getContentResolver()
                    .query(contactUri, queryFields, null, null, null)) {
                assert c != null;
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            }


        }
        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }


    }

    //Получаю имя подозреваемого выбранного ранее и с помощью этого имени получаю номер телефона из
//из записной книжки и отправляю неявный интент автоматически набирающий(без автоматического вызова)
//Если подозреваемый не был выбран, то выбрасываю тост предлагающий выграть подозреваемого
    private void readContacts() {

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + mCrime.getSuspect() + "%'";
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor c = Objects.requireNonNull(getActivity()).getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection, selection, null, null);
            try {
                assert c != null;
                if (c.getCount() == 0) {
                    Toast.makeText(getActivity(), R.string.toast_report, Toast.LENGTH_SHORT).show();
                    return;
                }
                c.moveToFirst();
                String number = c.getString(0);
                final Intent call = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + number));
                startActivity(call);

            } finally {
                assert c != null;
                c.close();
            }
        } catch (Exception e) {
            Log.e("CrimeFragment", "Eror to read contact");

        }

    }

    //Метод для обновления преступления
    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    //Метод для обновления времени
    private void updateTime() {
        mTimeButton.setText(mCrime.getTimeHumanReadable());
    }

    //Метод для обновления даты
    private void updateDate() {
        mDateButton.setText(mCrime.getDateHumanReadable());
    }

    //Метод для отправки объекта Crime в тектстовом виде через мессенджеры
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

    //Метод для обновления фото
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), Objects.requireNonNull(getActivity()));
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    //Необходимый интерфейс для активности-хоста.

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }
}
