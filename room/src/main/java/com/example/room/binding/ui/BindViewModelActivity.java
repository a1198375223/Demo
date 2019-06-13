package com.example.room.binding.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.room.R;
import com.example.room.binding.model.ProfileLiveDataViewModel;
import com.example.room.databinding.ActivityBindViewModelBinding;

public class BindViewModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProfileLiveDataViewModel viewModel = ViewModelProviders.of(this).get(ProfileLiveDataViewModel.class);

        ActivityBindViewModelBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_bind_view_model);

        // 为Binding设置ViewModel
        binding.setViewmodel(viewModel);

        // 为Binding设置LifecycleOwner
        binding.setLifecycleOwner(this);
    }
}
