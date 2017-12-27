package com.chepizhko.criminalintent;


import android.content.Intent;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }
    // CrimeListFragment будет вызывать этот метод в CrimeHolder.onClick(…),
    // а также тогда, когда пользователь выбирает команду создания новой записи преступления
    @Override
    public void onCrimeSelected(Crime crime) {
        // если используется телефонный интерфейс — запустить новый экземпляр CrimePagerActivity;
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            // а если используется планшетный интерфейс — поместить CrimeFragment в detail_fragment_container
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }

    }
    // CrimeListFragment.Callbacks для перезагрузки списка в onCrimeUpdated(Crime)
    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
