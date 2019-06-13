package com.example.room.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.room.dao.entity.User;

// 一旦数据发生变化就会该通知应用进行改变
public class UserViewModel extends ViewModel implements Parcelable {
    private String userId;
    private MutableLiveData<User> user;

    protected UserViewModel(Parcel in) {
        userId = in.readString();
    }

    public UserViewModel() {}

    public static final Creator<UserViewModel> CREATOR = new Creator<UserViewModel>() {
        @Override
        public UserViewModel createFromParcel(Parcel in) {
            return new UserViewModel(in);
        }

        @Override
        public UserViewModel[] newArray(int size) {
            return new UserViewModel[size];
        }
    };

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getUserId() {
        return userId;
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(MutableLiveData<User> user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserViewModel{" +
                "userId='" + userId + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
    }
}
