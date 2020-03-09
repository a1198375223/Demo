package com.example.androidxdemo.mianshi;

public class LruCache {
    private int[] keyArray;
    private int[] valueArray;
    private int[] countArray;

    public LruCache(int cacheSize) {
        keyArray = new int[cacheSize];
        for (int i=0; i<cacheSize; i++) {
            keyArray[i] = -1;
        }
        valueArray = new int[cacheSize];
        for (int i=0; i<cacheSize; i++) {
            valueArray[i] = -1;
        }
        for (int i=0; i<cacheSize; i++) {
            countArray[i] = 0;
        }
    }

    public int get(int key) {
        for (int i=0; i<keyArray.length; i++) {
            if (key == keyArray[i]) {
                countArray[i]++;
                return valueArray[i];
            }
        }
        return -1;
    }

    public void put(int key, int value) {
        int index = -1;
        for (int i=0; i<keyArray.length; i++) {
            if (keyArray[i] == -1) {
                index = i;
                break;
            }
            if (key == keyArray[i]) {
                valueArray[i] = value;
                return;
            }
        }
        if (index != -1) {
            keyArray[index] = key;
            valueArray[index] = value;
            countArray[index] = 0;
        } else {
            index = 0;
            int min = countArray[0];
            for (int i=1; i<countArray.length; i++) {
                if (countArray[i] < min) {
                    min = countArray[i];
                    index = i;
                }
            }
            keyArray[index] = key;
            valueArray[index] = value;
            countArray[index] = 0;
        }
    }
}
