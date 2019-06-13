package com.example.room.binding.model;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.example.room.BR;
import com.example.room.mvp.BaseViewModel;

public class ProfileObservableViewModel extends BaseViewModel {
    public ObservableField<String> name = new ObservableField<>("Ada");
    public ObservableField<String> lastName = new ObservableField<>("Lovelace");
    public ObservableInt likes = new ObservableInt(0);

    public ProfileObservableViewModel() {}

    public ProfileObservableViewModel(String name, String lastName, int likes) {
        this.name.set(name);
        this.lastName.set(lastName);
        this.likes.set(likes);
    }


    public void onLike() {
        likes.set(likes.get() + 1);
        notifyPropertyChanged(BR.popularity);
    }

    // 使用@Bindable来注解getter方法,是的可以使用notifyPropertyChanged(int field)方法
    @Bindable
    public Popularity getPopularity() {
        int like = likes.get();
        if (like > 9) {
            return Popularity.STAR;
        } else if (like > 4) {
            return Popularity.POPULAR;
        }
        return Popularity.NORMAL;
    }
}
