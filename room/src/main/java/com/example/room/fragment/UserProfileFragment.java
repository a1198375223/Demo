package com.example.room.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.room.R;
import com.example.room.dao.entity.User;
import com.example.room.dao.model.UserViewModel;

/**
 * 生命周期: onAttach -> onCreate -> onCreateView -> onViewCreated -> onActivityCreated -> onViewStateRestored-> onStart -> onResume 
 * ------home键切到桌面, 然后切回来
 * -> onPause -> onStop -> onSaveInstanceState //-> onStart -> onResume -> ... ->
 * ------back返回或者销毁
 * -> onPause -> onStop -> onDestroyView -> onDestroy -> onDetach
 */
public class UserProfileFragment extends Fragment {
    private static final String TAG = "UserProfileFragment";

    private UserViewModel mViewModel;

    private TextView mTv;
    private Button mChangeBn;
    private User mChangeUser;


    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            this.mViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        }
        mChangeUser = new User();
        mChangeUser.setUid(10001);
        mChangeUser.setFirstName("C");
        mChangeUser.setLastName("hange");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        mTv = view.findViewById(R.id.tv);
        mChangeBn = view.findViewById(R.id.change_bn);
        if (mViewModel != null) {
            mViewModel.getUser().observe(getActivity(), user -> {
                mTv.setText("model id=" + mViewModel.getUserId() + " uid=" + user.getUid() + " first name=" + user.getFirstName() + " last name=" + user.getLastName());
            });
        }
        mChangeBn.setOnClickListener(v -> mViewModel.getUser().setValue(mChangeUser));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewStateRestored: ");
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
    }
}
