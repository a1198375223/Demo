package com.example.androidxdemo.mianshi;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 流操作
 *
 */
public class StreamDetail {


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("Hollis", "HollisChuang", "hollis", "Hello", "HelloWorld", "Hollis");
        // 创建一个流
        Stream<String> stream = strings.stream();
        // 创建一个并行流
        Stream<String> parallelStream = strings.parallelStream();
        stream.filter(s -> !s.isEmpty()).forEach(System.out :: println);

    }
}
