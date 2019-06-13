package com.example.room.binding.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.room.binding.model.TimerViewModel;

public class TimerViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TimerViewModel.class)) {
            return (T) new TimerViewModel(new DefaultTimer());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
