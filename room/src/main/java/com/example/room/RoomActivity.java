package com.example.room;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.example.commonlibrary.utils.ThreadPool;
import com.example.room.binding.ui.BindingActivity;
import com.example.room.dao.entity.User;
import com.example.room.dao.model.UserViewModel;
import com.example.room.fragment.UserProfileFragment;
import com.example.room.paged.ui.SearchRepositoriesActivity;
import com.example.room.worker.WorkerActivity;


import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * activity生命周期 onCreate->onStart->onPostCreate->onResume->onResumeFragment->onAttachToWindow
 * ---------点击home 然后切回来
 * -> onPause -> onStop -> onSaveInstanceState(one) -> // -> onRestart -> onStart -> onResume -> onResumeFragment
 * --------- back销毁
 * -> onPause -> onStop -> onDestroy
 */
public class RoomActivity extends AppCompatActivity {
    private static final String TAG = "RoomActivity";


    private TextView mTv;
    private TextView mLiveTv;
    private Button mDeleteBn;
    private Button mAddBn;
    private Button mUpdateBn;
    private Button mQueryBn;
    private Button mShowBn;
    private Button mStartBn;
    private Button mStartBindBn;
    private Button mStartWorker;
    private Button mStartPaging;
    private EditText mLastNameEt, mFirstNameEt, mUserIdEt;

    private UserViewModel mModel;
    private MutableLiveData<User> mUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: state=" + getLifecycle().getCurrentState());
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: state=" + getLifecycle().getCurrentState());
        setContentView(R.layout.activity_room);

        mTv = findViewById(R.id.room_tv);
        mLiveTv = findViewById(R.id.live_tv);
        mDeleteBn = findViewById(R.id.delete_bn);
        mAddBn = findViewById(R.id.add_bn);
        mUpdateBn = findViewById(R.id.update_bn);
        mQueryBn = findViewById(R.id.query_bn);
        mShowBn = findViewById(R.id.show_bn);
        mStartBn = findViewById(R.id.start_fragment_bn);
        mLastNameEt = findViewById(R.id.last_name_et);
        mFirstNameEt = findViewById(R.id.first_name_et);
        mUserIdEt = findViewById(R.id.user_id_et);
        mStartBindBn = findViewById(R.id.start_bind_fragment_bn);
        mStartWorker = findViewById(R.id.start_worker);
        mStartPaging = findViewById(R.id.start_paging);

        mModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mModel.setUserId("00xx");
        User user1 = new User();
        user1.setUid(10000);
        user1.setFirstName("j");
        user1.setLastName("son");
        mUser = new MutableLiveData<>();
        mUser.setValue(user1);
        mModel.setUser(mUser);
        mUser.observe(this, user2 -> mLiveTv.setText("model id=" + mModel.getUserId() + " uid=" + user2.getUid() + " first name=" + user2.getFirstName() + " last name=" + user2.getLastName()));



        mDeleteBn.setOnClickListener(view -> {
            User user;
            if ((user = createUserById()) != null) {
                RoomUtils.getUserDao().delete(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "delete onSubscribe: ");
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "delete onComplete: ");

                                mTv.setText("delete onComplete: ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "delete onError: ", e);
                                mTv.setText("delete onError: " + e);
                            }
                        });
            }

        });

        mAddBn.setOnClickListener(view -> {
            User user;
            if ((user = createUserByName()) != null) {
                RoomUtils.getUserDao().insert(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "insert onSubscribe: ");
                            }

                            @Override
                            public void onSuccess(Long aLong) {
                                Log.d(TAG, "insert onSuccess: 插入成功:" + aLong);
                                mTv.setText("insert onSuccess: 插入成功:" + aLong);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "insert onError: ", e);
                                mTv.setText("insert onError: " + e);
                            }
                        });
            }
        });

        mUpdateBn.setOnClickListener(view -> {
            User user = createUser();
            if (user != null) {
                RoomUtils.getUserDao().update(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MaybeObserver<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "update onSubscribe: ");
                            }

                            @Override
                            public void onSuccess(Integer integer) {
                                Log.d(TAG, "update onSuccess: " + integer);
                                mTv.setText("update onSuccess: " + integer);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "update onError: ", e);
                                mTv.setText("update onError: " + e);
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "update onComplete: ");
                                mTv.setText("update onComplete: ");
                            }
                        });
            }
        });

        mQueryBn.setOnClickListener(view -> ThreadPool.runOnIOPool(()-> {
            String uid = mUserIdEt.getText().toString();
            if (TextUtils.isEmpty(uid)) {
                Toast.makeText(this, "please input uid", Toast.LENGTH_SHORT).show();
            }
            LiveData<User> live_user = RoomUtils.getUserDao().loadUserById(Integer.valueOf(uid));
            if (live_user != null) {
                ThreadPool.runOnUi(() -> {
                    live_user.observe(this,
                            user -> {
                                if (user == null) {
                                    mLiveTv.setText("update user is null");
                                } else {
                                    mLiveTv.setText("uid=" + user.getUid() + " first name=" + user.getFirstName() + " last name=" + user.getLastName());
                                }
                            });
                    clearText();
                });
            }
        }));


        mShowBn.setOnClickListener(view -> ThreadPool.runOnIOPool(()-> {
            List<User> list = RoomUtils.getUserDao().getAll();
            ThreadPool.runOnUi(() -> mTv.setText(new StringBuilder("show all->").append(list.toString())));
        }));


        mStartBn.setOnClickListener(view -> {
            UserProfileFragment fragment = new UserProfileFragment();

//            Bundle bundle = new Bundle();
//            bundle.putParcelable(UserProfileFragment.UID_KEY, mModel);
//            fragment.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.main_container, fragment);
            ft.addToBackStack(null);
            ft.commit();
        });

        mStartBindBn.setOnClickListener(view -> {
            Intent intent = new Intent(RoomActivity.this, BindingActivity.class);
            startActivity(intent);
        });

//        new BasePresenter(this) {
//            @Override
//            public void create(LifecycleOwner owner) {
//                super.create(owner);
//            }
//        };


        mStartWorker.setOnClickListener(view -> {
            Intent intent = new Intent(RoomActivity.this, WorkerActivity.class);
            startActivity(intent);
        });

        mStartPaging.setOnClickListener(view -> {
            Intent intent = new Intent(RoomActivity.this, SearchRepositoriesActivity.class);
            startActivity(intent);
        });
    }

    private User createUser() {
        String uid = mUserIdEt.getText().toString();
        String last_name = mLastNameEt.getText().toString();
        String first_name = mFirstNameEt.getText().toString();
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(last_name) || TextUtils.isEmpty(first_name)) {
            Toast.makeText(this, "please input uid last_name and first_name", Toast.LENGTH_SHORT).show();
            return null;
        }
        User user = new User();
        user.setUid(Integer.valueOf(uid));
        user.setLastName(last_name);
        user.setFirstName(first_name);
        clearText();
        return user;
    }

    private User createUserByName() {
        String last_name = mLastNameEt.getText().toString();
        String first_name = mFirstNameEt.getText().toString();
        if (TextUtils.isEmpty(last_name) || TextUtils.isEmpty(first_name)) {
            Toast.makeText(this, "please input last_name and first_name", Toast.LENGTH_SHORT).show();
            return null;
        }
        User user = new User();
        user.setLastName(last_name);
        user.setFirstName(first_name);
        mLastNameEt.setText("");
        mFirstNameEt.setText("");
        return user;
    }

    private User createUserById() {
        String uid = mUserIdEt.getText().toString();
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "please input uid", Toast.LENGTH_SHORT).show();
            return null;
        }
        User user = new User();
        user.setUid(Integer.valueOf(uid));
        mUserIdEt.setText("");
        return user;
    }

    private void clearText() {
        mFirstNameEt.setText("");
        mLastNameEt.setText("");
        mUserIdEt.setText("");
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: state=" + getLifecycle().getCurrentState());
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: state=" + getLifecycle().getCurrentState());
        super.onStart();
        Log.d(TAG, "onStart: state=" + getLifecycle().getCurrentState());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onPostCreate: state=" + getLifecycle().getCurrentState());
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: state=" + getLifecycle().getCurrentState());
        super.onResume();
        Log.d(TAG, "onResume: state=" + getLifecycle().getCurrentState());
    }

    @Override
    protected void onResumeFragments() {
        Log.d(TAG, "onResumeFragments: state=" + getLifecycle().getCurrentState());
        super.onResumeFragments();
    }

    @Override
    public void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow: state=" + getLifecycle().getCurrentState());
        super.onAttachedToWindow();
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: state=" + getLifecycle().getCurrentState());
        super.onPause();
        Log.d(TAG, "onPause: state=" + getLifecycle().getCurrentState());
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: state=" + getLifecycle().getCurrentState());
        super.onStop();
        Log.d(TAG, "onStop: state=" + getLifecycle().getCurrentState());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: state=" + getLifecycle().getCurrentState());
        super.onDestroy();
        Log.d(TAG, "onDestroy: state=" + getLifecycle().getCurrentState());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: one state=" + getLifecycle().getCurrentState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: state=" + getLifecycle().getCurrentState());
    }
}
