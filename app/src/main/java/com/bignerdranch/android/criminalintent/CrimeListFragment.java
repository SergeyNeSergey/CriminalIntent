package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

//Класс-фрагмент активностью хостом которого является CrimeListActivity. представляет собой RecyclerView
public class CrimeListFragment extends Fragment {
    //Ключ для сохраниния состояния видимости подзаголовка меню.
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;
    private UUID mPositionCrimeForSaveChanges;

    //В момент прикрепления фрагмента к активности хосту
// выполняю непроверяемое преобразование своей активности к CrimeListFragment.Callbacks .
// Это означает, что активность-хост должна реализовать CrimeListFragment.Callbacks.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    //В момент создания фрагмента прикрепляю меню.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //В методе создания отображения фрагмента связываю фрагмент с RecyclerView и задаю видимость подзаголовку
//Вызываю метод обновляющий представление.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container,
                false);
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean
                    (SAVED_SUBTITLE_VISIBLE);
        }
        mCrimeRecyclerView = (RecyclerView) view
                .findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager
                (getActivity()));
        updateUI();
        return view;
    }

    //Вызываю метод обновляющий представление.
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    //В методе открепления обнуляю колбек.
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    // В меню описываю логику нажатия на значки. В одном случае добавляю новый объект Crime
//в другом скрываю/показываю подзаголовок с количеством объектов Crime
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Сохраняю значение состояния подзаголовка
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    //Создаю меню указываю описание подзаголовка в зависимости от его состояния
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    //Описываю логику работы ползаголовка
    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeSize = crimeLab.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeSize, crimeSize);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        Objects.requireNonNull(activity.getSupportActionBar()).setSubtitle(subtitle);
    }

    //Описываю логику обновления фрагмента.
    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        Crime crime = new Crime();
        if (mAdapter == null || crimeLab.getCrime(mPositionCrimeForSaveChanges) == null) {
            mAdapter = new CrimeAdapter(crimes);
            mPositionCrimeForSaveChanges = crime.getId();

            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyItemChanged(crimes.indexOf(crimeLab.getCrime(mPositionCrimeForSaveChanges)));
        }
        updateSubtitle();
    }

    // Обязательный интерфейс для активности-хоста.
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    //Данный CrimeHolder обладает двумя конструкторами. В зависимости от от того, сделал ли пользователь метку
// о необходимости вызова полиции или нет разные конструкторы удерживают
//разные объекты  View.
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Button mCallToPolice;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int n) {
            super(inflater.inflate(R.layout.list_item_crime_to_call_the_police, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.cp_crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.cp_crime_date);
            mCallToPolice = (Button) itemView.findViewById(R.id.call_police);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.cp_crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDateHumanReadable());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE :
                    View.GONE);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onCrimeSelected(mCrime);
            mPositionCrimeForSaveChanges = mCrime.getId();

        }
    }

    //CrimeAdapter вызывает разные конструкторы CrimeHolder. В методе getItemViewType задается viewType
//который зависит от isRequiresPolice() объекта Crime.
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if (viewType == 1) return new CrimeHolder(layoutInflater, parent, 1);
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public int getItemViewType(int position) {
            if (mCrimes.get(position).isRequiresPolice()) return 1;
            return super.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }

}
