package com.example.androidxdemo.mianshi;

public class CircleQueue {
    private int[] queue;
    private int writeIndex;
    private int readIndex;
    private int tag = 0;

    public CircleQueue(int initializeSize) {
        this.queue = new int[initializeSize];
        writeIndex = 0;
        readIndex = 0;
    }

    public int take() {
        if (empty())
            return -1;
        int result = queue[readIndex];
        readIndex = (readIndex + 1) % queue.length;
        tag = 0;
        return result;
    }

    public boolean put(int value) {
        if (full())
            return false;
        queue[writeIndex] = value;
        writeIndex = (writeIndex + 1) % queue.length;
        tag = 1;
        return true;
    }

    public boolean empty() {
        return readIndex == writeIndex && tag == 0;
    }

    public boolean full() {
        return readIndex == writeIndex && tag == 1;
    }
}
