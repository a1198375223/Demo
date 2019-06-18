package com.example.androidxdemo.activity.camera;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends Fragment {
    private static final String TAG = "PermissionsFragment";
    private NavOptions navOptions = new NavOptions.Builder()
            .setPopUpTo(R.id.permissionsFragment, true)
            .build();

    // 权限
    private final String[] PERMISSIONS_REQUIRED = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private final int PERMISSIONS_REQUEST_CODE = 10;


    public PermissionsFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkPermissions()) {
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE);
        } else {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                    .navigate(R.id.action_permissionsFragment_to_cameraFragment, null, navOptions);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toasty.showSuccess("权限授权成功!");
                Navigation.findNavController(requireActivity(), R.id.fragment_container)
                        .navigate(R.id.action_permissionsFragment_to_cameraFragment, null, navOptions);
            } else {
                Toasty.showError("权限授权失败!");
            }
        }
    }

    private boolean checkPermissions() {
        for (int i = 0; i < PERMISSIONS_REQUIRED.length; i++) {
            if (ContextCompat.checkSelfPermission(requireContext(), PERMISSIONS_REQUIRED[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
