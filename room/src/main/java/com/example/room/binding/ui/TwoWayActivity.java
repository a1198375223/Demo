package com.example.room.binding.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.room.R;
import com.example.room.binding.model.TimerViewModel;
import com.example.room.binding.util.TimerViewModelFactory;
import com.example.room.databinding.ActivityTwoWayBinding;

public class TwoWayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTwoWayBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_two_way);
        TimerViewModel viewModel = ViewModelProviders.of(this, new TimerViewModelFactory()).get(TimerViewModel.class);
        binding.setViewmodel(viewModel);

    }
}
