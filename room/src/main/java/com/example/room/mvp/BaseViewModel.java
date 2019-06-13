package com.example.room.mvp;

import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel是一个负责管理Activity和Fragment数据的类. 还处理activity / fragment和其他application的通信.
 * 使用ViewModel的目的就是为了获取和保存Activity / Fragment中必要的数据.
 * ViewModel唯一的职责是管理UI的数据。 它永远不应访问您的视图层次结构或将引用保存回Activity或Fragment
 *
 * ViewModel 还可以进行Activity和Fragment的通信
 * UserModel userModel = ViewModelProviders.of(getActivity()).get(UserModel.class);
 *
 * ViewModel 还可以在binding中的xml中使用, 例如:
 * <TextView
 *     android:text="@{viewmodel.userName}" />
 */
public class BaseViewModel extends ViewModel implements IModel {
    private static final String TAG = "BaseViewModel";

    // 用来保存callback 如果model实现序列化 则这个变量不参与序列化
    private transient PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    // 用于最后的回收数据
    @Override
    protected void onCleared() {
        super.onCleared();
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    // notify all callback 通知所有的callback去更新
    public void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    // 通知所有的callback fieldId指定的属性发生了改变. 该属性的getter方法必须被@Bindable注解标注,
    // 在BR中生成field来使用fieldId
    public void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}
