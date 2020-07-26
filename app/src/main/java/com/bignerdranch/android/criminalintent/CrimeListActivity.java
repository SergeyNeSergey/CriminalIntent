package com.bignerdranch.android.criminalintent;

import android.content.Intent;

import androidx.fragment.app.Fragment;

// Активность хост для CrimeListFragment при меньшей стороне экрана <600dp. И для CrimeListFragment
// и CrimeFragment при меньшей стороне экрана >600dp
public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    //Ссылаюсь на ресурс-псевдоним который ссылается на два разных макета в зависимости от
    //ширины экрана
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    //Переопределённый метод из CrimeListFragment.Callbacks. Если заполняется detail_fragment_container
    //то CrimeFragment перенаправляется для присоединения к detail_fragment_container.
    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    //Переопределённый метод из CrimeFragment.Callbacks. обновляет CrimeListFragment при изменении
    //CrimeFragment
    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        assert listFragment != null;
        listFragment.updateUI();

    }
}
