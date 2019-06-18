package com.example.androidxdemo.activity.camera;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;

public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";
    private static final String FILE_NAME_KEY = "file_name";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new ImageView(getContext());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            File file = new File(getArguments().getString(FILE_NAME_KEY));
            Glide.with(this).load(file).into((ImageView) view);
        }
    }

    public static PhotoFragment createPhotoFragment(File file) {
        Log.d(TAG, "createPhotoFragment: ");
        PhotoFragment fragment = new PhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILE_NAME_KEY, file.getAbsolutePath());
        fragment.setArguments(bundle);
        return fragment;
    }
}
