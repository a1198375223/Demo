package com.example.room.binding.model;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.room.mvp.BaseViewModel;

public class ProfileLiveDataViewModel extends BaseViewModel {
    private static final String TAG = "ProfileLiveDataViewMode";

    public MutableLiveData<String> name = new MutableLiveData<>("Ada");
    public MutableLiveData<String> lastName = new MutableLiveData<>("Lovelace");
    public MutableLiveData<Integer> likes = new MutableLiveData<>(0);

    public LiveData<Popularity> popularity = Transformations.map(likes, input -> {
        if (input > 9) {
            return Popularity.STAR;
        } else if (input > 4) {
            return Popularity.POPULAR;
        }
        return Popularity.NORMAL;
    });

    // 为likes + 1
    public void onLike() {
        if (likes.getValue() != null) {
            Log.d(TAG, "onLike: now=" + likes.getValue());
            likes.setValue(likes.getValue() + 1);
        }
    }
}
