package com.example.room.binding.model;

import android.util.Log;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableInt;

import com.example.room.BR;
import com.example.room.binding.util.Timer;
import com.example.room.mvp.BaseViewModel;

import java.util.TimerTask;


public class TimerViewModel extends BaseViewModel {
    private static final String TAG = "TimerViewModel";
    public static final int INITIAL_SECONDS_PER_WORK_SET = 5; // seconds
    public static final int INITIAL_SECONDS_PER_REST_SET = 2; // seconds
    public static final int INITIAL_NUMBER_OF_SETS = 5; // 循环的次数


    public ObservableInt timePerWorkSet = new ObservableInt(INITIAL_SECONDS_PER_WORK_SET * 10); // work时间的总数
    public ObservableInt timePerRestSet = new ObservableInt(INITIAL_SECONDS_PER_REST_SET * 10); // rest时间的总数
    public ObservableInt workTimeLeft = new ObservableInt(timePerWorkSet.get()); // 当前work时间
    public ObservableInt restTimeLeft = new ObservableInt(timePerRestSet.get()); // 当前rest时间



    // 统计倒计时的次数 相关数据
    private int numberOfSetsTotal = INITIAL_NUMBER_OF_SETS;
    private int numberOfSetsElapsed = 0;

    //------------getter & setter-------------
    @Bindable
    public int[] getNumberOfSets() {
        return new int[]{numberOfSetsElapsed, numberOfSetsTotal};
    }

    public void setNumberOfSets(int[] numberOfSets) {
        int newTotal = numberOfSets[1];
        if (newTotal == numberOfSetsTotal)
            return;
        if (newTotal != 0 && newTotal > numberOfSetsElapsed) {
            this.numberOfSetsElapsed = numberOfSets[0];
            numberOfSetsTotal = newTotal;
        }
        notifyPropertyChanged(BR.numberOfSets);
    }

    // 点击总数减一的方法
    public void setsDecrease() {
        if (numberOfSetsTotal > numberOfSetsElapsed + 1) {
            numberOfSetsTotal -= 1;
            notifyPropertyChanged(BR.numberOfSets);
        }
    }

    // 点击总数加一的方法
    public void setsIncrease() {
        numberOfSetsTotal += 1;
        notifyPropertyChanged(BR.numberOfSets);
    }
    //----------------------------------------

    // 计时器的状态机相关数据
    // 计时器的状态机
    public enum TimerStates {STOPPED, STARTED, PAUSED,}
    // 计时器的状态,工作还是休息
    public enum StartedStages {WORKING, RESTING}
    // 计时器状态 start pause stop
    public boolean timerRunning;
    public boolean isWorkingStage;
    private TimerStates state = TimerStates.STOPPED;
    private StartedStages stage = StartedStages.WORKING;
    //------------getter & setter-------------
    @Bindable
    public boolean getTimerRunning() {
        return state == TimerStates.STARTED;
    }

    @Bindable
    public boolean getIsWorkingStage() {
        return stage == StartedStages.WORKING;
    }

    public void setTimerRunning(boolean timerRunning) {
        if (timerRunning) {
            startButtonClicked();
        } else {
            pauseButtonClicked();
        }
    }
    //----------------------------------------

    private Timer timer;

    public TimerViewModel(Timer timer) {
        this.timer = timer;
    }



    // 开始的按钮被点击事件
    private void startButtonClicked() {
        if (state == TimerStates.STARTED) {
            Log.d(TAG, "startButtonClicked: do nothing");
        } else if (state == TimerStates.STOPPED) {
            stoppedToStarted();
        } else {
            pausedToStarted();
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (state == TimerStates.STARTED) {
                    updateCountdowns();
                }
            }
        };

        timer.start(task);
    }

    // 暂停按钮被点击事件
    private void pauseButtonClicked() {
        if (state == TimerStates.STARTED) {
            startedToPaused();
        }
    }

    // 停止按钮被点击事件
    public void stopButtonClicked() {
        resetTimers();
        numberOfSetsElapsed = 0;
        state = TimerStates.STOPPED;
        stage = StartedStages.WORKING;
        timer.reset();

        notifyPropertyChanged(BR.timerRunning);
        notifyPropertyChanged(BR.isWorkingStage);
        notifyPropertyChanged(BR.numberOfSets);
    }


    // 计时器状态从stopped -> started
    private void stoppedToStarted() {
        timer.resetStartTime();
        state = TimerStates.STARTED;
        stage = StartedStages.WORKING;

        notifyPropertyChanged(BR.isWorkingStage);
        notifyPropertyChanged(BR.timerRunning);
    }

    // 计时器状态从paused -> started
    private void pausedToStarted() {
        timer.updatePausedTime();
        state = TimerStates.STARTED;

        notifyPropertyChanged(BR.timerRunning);
    }

    // 计时器状态从started -> paused
    private void startedToPaused() {
        state = TimerStates.PAUSED;
        timer.resetPauseTime();

        notifyPropertyChanged(BR.timerRunning);
    }

    // 计时器状态从 Working -> Resting
    private void workoutFinished() {
        timer.resetStartTime();
        stage = StartedStages.RESTING;

        notifyPropertyChanged(BR.isWorkingStage);
    }

    // 计时器状态从 Resting -> Working
    private void setFinish() {
        timer.resetStartTime();
        stage = StartedStages.WORKING;

        notifyPropertyChanged(BR.isWorkingStage);
        notifyPropertyChanged(BR.numberOfSets);
    }

    // 计时器状态从 Resting -> Stopped
    private void timerFinished() {
        timer.reset();
        state = TimerStates.STOPPED;
        stage = StartedStages.WORKING;
        numberOfSetsElapsed = 0;

        notifyPropertyChanged(BR.timerRunning);
        notifyPropertyChanged(BR.isWorkingStage);
        notifyPropertyChanged(BR.numberOfSets);
    }

    // 更新倒计时
    private void updateCountdowns() {
        if (state == TimerStates.STOPPED) {
            resetTimers();
            return;
        }

        // 获取当前的时间
        long elapsed = state == TimerStates.PAUSED ? timer.getPausedTime() : timer.getElapsedTime();

        if (stage == StartedStages.RESTING) {
            updateRestCountdowns(elapsed);
        } else {
            updateWorkCountdowns(elapsed);
        }
    }

    // 更新rest的时间
    private void updateRestCountdowns(long elapsed) {
        long newRestTimeLeft = timePerRestSet.get() - (elapsed / 100);

        restTimeLeft.set(newRestTimeLeft > 0 ? (int) newRestTimeLeft : 0);

        if (newRestTimeLeft <= 0) {
            numberOfSetsElapsed += 1;
            resetTimers();
            if (numberOfSetsElapsed >= numberOfSetsTotal) { // end
                timerFinished();
            } else {
                setFinish();
            }
        }
    }

    // 更新work时间更新
    private void updateWorkCountdowns(long elapsed) {
        stage = StartedStages.WORKING;
        long newTimeLeft = timePerWorkSet.get() - (elapsed / 100);
        if (newTimeLeft <= 0) {
            workoutFinished();
        }
        workTimeLeft.set(newTimeLeft > 0 ? (int) newTimeLeft : 0);
    }


    // 重置时间
    private void resetTimers() {
        workTimeLeft.set(timePerWorkSet.get());

        restTimeLeft.set(timePerRestSet.get());
//        workTimeLeft.notifyChange();
//        restTimeLeft.notifyChange();
    }


    // 点击增加工作时间的事件
    public void workTimeIncrease() {
        timePerSetIncrease(timePerWorkSet, 1, 0);
    }

    // 点击增加rest时间的事件
    public void restTimeIncrease() {
        timePerSetIncrease(timePerRestSet, 1, 0);
    }

    // 点击减少work时间事件
    public void workTimeDecrease() {
        timePerSetIncrease(timePerWorkSet, -1, 10);
    }

    // 点击减少rest时间事件
    public void restTimeDecrease() {
        timePerSetIncrease(timePerRestSet, -1, 0);
    }


    private void timePerSetIncrease(ObservableInt timePerSet, int sign, int min) {
        if (timePerSet.get() < 10 && sign < 0) {
            return;
        }
        roundTimeIncrease(timePerSet, sign, min);

        if (state == TimerStates.STOPPED) {
            resetTimers();
        } else {
            updateCountdowns();
        }
    }

    private void roundTimeIncrease(ObservableInt observableInt, int sign, int min) {
        int currentValue = observableInt.get();
        int newValue;

        if (currentValue < 100) {
            newValue = observableInt.get() + sign * 10;
        } else if (currentValue < 600) {
            newValue = Math.round(currentValue / 50.0f) * 50 + (50 * sign);
        } else {
            newValue = Math.round(currentValue / 100f) * 100 + (100 * sign);
        }
        newValue = newValue > min ? newValue : min;
        observableInt.set(newValue);
    }
}
