package com.example.androidxdemo.activity.image.qr_code;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.commonlibrary.view.ShortcutShareView;

public class ContainerFragment extends Fragment {
    private static final String TAG = "ContainerFragment";
    private ShortcutShareView mShareView;
    
    public ContainerFragment() {
        Log.d(TAG, "ContainerFragment: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mShareView = new ShortcutShareView(requireContext());
        mShareView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return mShareView;
    }

    public ShortcutShareView createShortcutShareView(Bitmap shortcut, Bitmap qrCode, String logo) {
        mShareView.createShortcutShareView(shortcut, qrCode, logo);
        return mShareView;
    }
}
