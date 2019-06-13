package com.example.androidxdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CommonFragment extends Fragment {
    private TextView mTextView;
    private String text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        mTextView = (TextView) view.findViewById(R.id.text);
        mTextView.setText(text);
        return view;
    }

    public void setText(String text) {
        this.text = text;
    }
}
