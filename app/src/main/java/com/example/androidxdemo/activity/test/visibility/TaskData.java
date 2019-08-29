package com.example.androidxdemo.activity.test.visibility;

public class TaskData {
    public int code;

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "TaskData{" +
                "code=" + code +
                '}';
    }
}