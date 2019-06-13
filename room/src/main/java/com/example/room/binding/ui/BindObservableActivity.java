package com.example.room.binding.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.room.R;
import com.example.room.binding.model.ProfileObservableViewModel;
import com.example.room.databinding.ActivityBindObservableBinding;

public class BindObservableActivity extends AppCompatActivity {
    private static final String TAG = "BindObservableActivity";

    private ProfileObservableViewModel mUser = new ProfileObservableViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBindObservableBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_bind_observable);
        binding.setUser(mUser);
    }


    public void onLike(View view) {
        mUser.likes.set(mUser.likes.get() + 1);
    }
}
