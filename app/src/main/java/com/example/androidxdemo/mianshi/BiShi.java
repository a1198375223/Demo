package com.example.androidxdemo.mianshi;

import android.os.Build;
import android.util.LruCache;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;


public class BiShi {



    public static void main(String[] args) {
        Integer A = 1;
        Integer B = 1;
        Integer C = new Integer(1);
        Integer D = 128;
        Integer E = 128;
        System.out.println("A==B" + (A == B));
        System.out.println("A.equals(B)" + A.equals(B));
        System.out.println("A==C" + (A == C));
        System.out.println("A.equals(C)" + A.equals(C));
        System.out.println("D==E" + (D == E));
        System.out.println("D.equals(E)" + D.equals(E));
    }
}
