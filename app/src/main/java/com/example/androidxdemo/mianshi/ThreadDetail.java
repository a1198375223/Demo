package com.example.androidxdemo.mianshi;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * wait: 释放当前锁，阻塞直到被notify或notifyAll唤醒，或者超时，或者线程被中断(InterruptedException)
 * notify: 任意选择一个（无法控制选哪个）正在这个对象上等待的线程把它唤醒，其它线程依然在等待被唤醒
 * notifyAll: 唤醒所有线程，让它们去竞争，不过也只有一个能抢到锁
 * sleep: 不是Object中的方法，而是Thread类的静态方法，让当前线程持有锁阻塞指定时间
 *
 * tips:
 * 1. wait必须在synchronized同步代码块中，否则会抛出异常IllegalMonitorStateException，notify也是如此，可以说wait和notify是就是为了在同步代码中做线程调度而生的。
 *
 * CountDownLatch
 */
public class ThreadDetail {

    public static class SameThreadSync implements Runnable{

        @Override
        public void run() {
            synchronized (SameThreadSync.class) {
                try {
                    TimeUnit.MILLISECONDS.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("currentTime: " + System.currentTimeMillis() + "  " + Thread.currentThread().getName() + " has finished now");
            }
        }
    }

    // 日志行号记录

    private AtomicInteger count = new AtomicInteger();


    private final static int threadCount = 200;

    private static void test(int threadNum) throws Exception {
        Thread.sleep(100);
        System.out.println(threadNum);
        Thread.sleep(100);
    }
    private static CyclicBarrier barrier = new CyclicBarrier(5);

    private static void race(int threadNum) throws Exception {
        Thread.sleep(1000);
        System.out.println("{} is ready" + threadNum);
        barrier.await();
        System.out.println("{} continue" + threadNum);
    }
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            final int threadNum = i;
            Thread.sleep(1000);
            executor.execute(() -> {
                try {
                    race(threadNum);
                } catch (Exception e) {
                    System.out.println("exception" +  e);
                }
            });
        }
        executor.shutdown();
        ExecutorService exec = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            exec.execute(() -> {
                try {
                    test(threadNum);
                } catch (Exception e) {
                    System.out.println("exception");
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println("finish");
        exec.shutdown();
//        ThreadDetail main = new ThreadDetail();
//        Thread thread = new Thread();
//
//        // 开启两个线程去执行test方法
////        new Thread(main::test).start();
////        new Thread(main::test).start();
//        main.test();
    }

    private synchronized void test() {
        try {
            log("进入了同步方法，并开始睡觉，1s");
            // sleep不会释放锁，因此其他线程不能进入这个方法
            Thread.sleep(1000);
            log("睡好了，但没事做，有事叫我，等待2s");
            //阻塞在此，并且释放锁，其它线程可以进入这个方法
            //当其它线程调用此对象的notify或者notifyAll时才有机会停止阻塞
            //就算没有人notify，如果超时了也会停止阻塞
            wait(2000);
            log("我要走了，但我要再睡一觉，10s");
            //这里睡的时间很长，因为没有释放锁，其它线程就算wait超时了也无法继续执行
            Thread.sleep(1000);
            log("走了");
//            notify();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 打印日志
    private void log(String s) {
        System.out.println(count.incrementAndGet() + " "
                + new Date().toString().split(" ")[3]
                + "\t" + Thread.currentThread().getName() + " " + s);
    }


//    public static void main(String[] args) {
//        SameThreadSync runnable = new SameThreadSync();
//        Thread thread1 = new Thread(runnable, "Amy thread");
//        Thread thread2 = new Thread(runnable, "Bob thread");
//
//        System.out.println("currentTime: " + System.currentTimeMillis());
//        thread1.start();
//        thread2.start();
//    }
}
