package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.UUID;

// Активность хост для CrimeFragment при меньшей стороне экрана <600dp. Реализует ViewPager2.
public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks {
    //Ключ для Intent по которому передается ID объекта Crime, что является первичным ключом записи в БД.
    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";
    private ViewPager2 mViewPager;
    private List<Crime> mCrimes;
    private Button mToFirst;
    private Button mToLast;

    //Метод для создания интента к данной активности. Вызывается из сторонних активностей.
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    //В методе создания активности описываю логику работы ViewPager2 и добавляю к кнопкам ссылки на
//кнопки в макете.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager = findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();
        mToFirst = findViewById(R.id.button_first_crime);

        mToLast = findViewById(R.id.button_last_crime);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStateAdapter(fragmentManager, getLifecycle()) {

            @Override
            public int getItemCount() {
                return mCrimes.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }
        });


        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }


        }


    }

    //В методе onResume() описываю логику работы кнопок.
    @Override
    public void onResume() {
        super.onResume();
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    mToFirst.setEnabled(false);
                } else {
                    mToFirst.setEnabled(true);
                }
                if (position == mCrimes.size() - 1) {
                    mToLast.setEnabled(false);
                } else {
                    mToLast.setEnabled(true);
                }
            }

        });
        mViewPager.refreshDrawableState();

        mToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mToLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });


    }

    //Переопределённый метод из CrimeFragment.Callbacks. обновляет CrimeListFragment при изменении
//CrimeFragment. В данном случае является заглушкой так как данная активность используется только при
//заполнении активности ровно один фрагментом. Но тем не менее его необходимо вызвать и переопределить.
    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}

