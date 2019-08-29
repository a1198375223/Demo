package com.example.androidxdemo.mianshi;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.BooleanSupplier;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Rxjava2使用的是观察者模式
 */
@SuppressLint("CheckResult")
public class Rxjava2Detail {

    // -----------------------------------创建操作符------------------------------------------------

    // 1. create 用来创建一个Observable对象
    public void create() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("hhhh");
                emitter.onComplete();
            }
        });
    }

    // 2. just 创建一个Observable对象并发送事件 不可传入超过10个事件
    public void just() {
        Observable.just(1, 2, 3);
    }


    // 3. fromArray()与just相似, 但是可以传入超过10个事件
    public void fromArray() {
        Observable.fromArray("hh", "11", "22");
    }

    // 4. fromCallable() Callable 和 Runnable 的用法基本一致，只是它会返回一个结果值，这个结果值就是发给观察者的。
    public void fromCallable() {
        Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        });
    }

    // 5. fromFuture() Future 的作用是增加了 cancel() 等方法操作 Callable，它可以通过 get() 方法来获取 Callable 返回的值。
    public void fromFuture() {
        Observable.fromFuture(new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        }));
    }

    // 6. fromIterable() 直接发送一个 List 集合数据给观察者
    public void fromIterable() {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        Observable.fromIterable(list);
    }

    // 7. defer() 这个方法的作用就是直到被观察者被订阅后才会创建被观察者。
    public void defer() {
        Observable.defer(new Callable<ObservableSource<?>>() {
            @Override
            public ObservableSource<?> call() throws Exception {
                return Observable.just(1);
            }
        });
    }

    // 8. timer() 当到指定时间后就会发送一个 0L 的值给观察者。
    public void timer() {
        Observable.timer(2, TimeUnit.SECONDS);
    }

    // 9. interval() 每隔一段时间就会发送一个事件，这个事件是从0开始，不断增1的数字。
    public void interval() {
        Observable.interval(2, 3, TimeUnit.SECONDS);
    }

    // 10. intervalRange() 可以指定发送事件的开始值和数量，其他与 interval() 的功能一样。
    public void intervalRange() {
        // 从2开始发送5个连续的数据
        Observable.intervalRange(2, 5, 2, 1, TimeUnit.SECONDS);
    }

    // 11. range() 同时发送一定范围的事件序列
    public void range() {
        Observable.range(2, 5);
    }

    // 12. rangeLong() 作用与 range() 一样，只是数据类型为 Long
    public void rangeLong() {
        Observable.rangeLong(2, 5);
    }


    // 13. empty() 直接发送 onComplete() 事件
    public void empty() {
        Observable.empty();
    }


    // 14. never() 不发送任何事件
    public void never() {
        Observable.never();
    }

    // 15. error() 发送 onError() 事件
    public void error() {
        Observable.error(new Callable<Throwable>() {
            @Override
            public Throwable call() throws Exception {
                return null;
            }
        });
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------转化操作符---------------------------------------------

    // 1. map() 可以将被观察者发送的数据类型转变成其他的类型
    public void map() {
        Observable.just(1, 2, 3)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return String.valueOf(integer);
                    }
                });
    }

    // 2. flatMap() 这个方法可以将事件序列中的元素进行整合加工，返回一个新的被观察者。
    public void flatMap() {
        List<Person> personList = new ArrayList<>();
        Observable.fromIterable(personList)
                .flatMap(new Function<Person, ObservableSource<Plan>>() {
                    @Override
                    public ObservableSource<Plan> apply(Person person) throws Exception {
                        return Observable.fromIterable(person.getPlanList());
                    }
                })
                .flatMap(new Function<Plan, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Plan plan) throws Exception {
                        return Observable.fromIterable(plan.getActionList());
                    }
                });
    }

    // 3. concatMap() concatMap() 和 flatMap() 基本上是一样的，只不过 concatMap() 转发出来的事件是有序的，而 flatMap() 是无序的。
    public void concatMap() {
        List<Person> personList = new ArrayList<>();
        Observable.fromIterable(personList)
                .concatMap(new Function<Person, ObservableSource<Plan>>() {
                    @Override
                    public ObservableSource<Plan> apply(Person person) throws Exception {
                        return Observable.fromIterable(person.getPlanList());
                    }
                })
                .concatMap(new Function<Plan, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Plan plan) throws Exception {
                        return Observable.fromIterable(plan.getActionList());
                    }
                });
    }

    // 4. buffer() 从需要发送的事件当中获取一定数量的事件，并将这些事件放到缓冲区当中一并发出。
    public void buffer() {
        Observable.just(1, 2, 3, 4, 5)
                .buffer(2, 1); // count 缓冲区元素的数量，skip 就代表缓冲区满了之后，发送下一次事件序列的时候要跳过多少元素。
        // 1 2 ---> 2 3 ---> 3 4 ---> 4 5 ---> 5
    }

    // 5. groupBy() 将发送的数据进行分组，每个分组都会返回一个被观察者
    public void groupBy() {
        Observable.just(5, 2, 3, 4, 1, 5, 8, 9, 7, 10)
                .groupBy(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        return integer % 3;
                    }
                })
                .subscribe(new Observer<GroupedObservable<Integer, Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupedObservable<Integer, Integer> integerIntegerGroupedObservable) {
                        integerIntegerGroupedObservable.subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Integer integer) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // 6. scan() 将数据以一定的逻辑聚合起来
    public void scan() {
        // 逐步累加
        // 1 + 2 ---> 1 + 2 + 3 ---> ...
        Observable.just(1, 2, 3, 4, 5)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                });
    }

    // 7. window() 发送指定数量的事件时，就将这些事件分为一组。window 中的 count 的参数就是代表指定的数量，
    //    例如将 count 指定为2，那么每发2个数据就会将这2个数据分成一组。
    public void window() {
        // 1 2一组  3 4一组 5一组
        Observable.just(1, 2, 3, 4, 5)
                .window(2)
                .subscribe(new Observer<Observable<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Observable<Integer> integerObservable) {
                        integerObservable.subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Integer integer) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------组合操作符---------------------------------------------

    // 1. concat() 可以将多个观察者组合在一起，然后按照之前发送顺序发送事件。需要注意的是，concat() 最多只可以发送4个事件
    public void concat() {
        Observable.concat(Observable.just(1, 2),
                Observable.just(3, 4),
                Observable.just(5, 6),
                Observable.just(7, 8));
    }

    // 2. concatArray() 与 concat() 作用一样，不过 concatArray() 可以发送多于 4 个被观察者。
    public void concatArray() {
        Observable.concatArray(Observable.just(1, 2),
                Observable.just(3, 4),
                Observable.just(5, 6),
                Observable.just(7, 8),
                Observable.just(9, 10));
    }

    // 3. merge() 这个方法月 concat() 作用基本一样，知识 concat() 是串行发送事件，而 merge() 并行发送事件
    public void merge() {
        Observable.merge(
                Observable.interval(1, TimeUnit.SECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(Long aLong) throws Exception {
                        return "A" + aLong;
                    }
                }),
                Observable.interval(1, TimeUnit.SECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(Long aLong) throws Exception {
                        return "B" + aLong;
                    }
                }));
    }

    // 4. concatArrayDelayError() 在 concatArray() 和 mergeArray() 两个方法当中，如果其中有一个被观察者
    //    发送了一个 Error 事件，那么就会停止发送事件，如果你想 onError() 事件延迟到所有被观察者都发送完事件后
    //    再执行的话，就可以使用  concatArrayDelayError() 和 mergeArrayDelayError()
    public void concatArrayDelayError() {
        Observable.concatArrayDelayError(
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(1);
                        e.onError(new NumberFormatException());
                    }
                }),
                Observable.just(2, 3, 4));
    }

    // 5. zip() 会将多个被观察者合并，根据各个被观察者发送事件的顺序一个个结合起来，最终发送的事件数量会与源 Observable 中最少事件的数量一样。
    public void zip() {
        Observable.zip(Observable.intervalRange(1, 5, 1, 1, TimeUnit.SECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                String s1 = "A" + aLong;
                                return s1;
                            }}),
                Observable.intervalRange(1, 6, 1, 1, TimeUnit.SECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                String s2 = "B" + aLong;
                                return s2;
                            }
                        }),
                new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String s, String s2) throws Exception {
                        String res = s + s2;
                        return res;
                    }
                });
    }


    // 6. combineLatest() combineLatest() 的作用与 zip() 类似，但是 combineLatest() 发送事件的序列是与发送
    //    的时间线有关的，当 combineLatest() 中所有的 Observable 都发送了事件，只要其中有一个 Observable 发送
    //    事件，这个事件就会和其他 Observable 最近发送的事件结合起来发送，这样可能还是比较抽象，看看以下例子代码。
    public void combineLatest() {
        Observable.combineLatest(
                Observable.intervalRange(1, 4, 1, 1, TimeUnit.SECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                String s1 = "A" + aLong;
                                return s1;
                            }
                        }),
                Observable.intervalRange(1, 5, 2, 2, TimeUnit.SECONDS)
                        .map(new Function<Long, String>() {
                            @Override
                            public String apply(Long aLong) throws Exception {
                                String s2 = "B" + aLong;
                                return s2;
                            }
                        }),
                new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String s, String s2) throws Exception {
                        String res = s + s2;
                        return res;
                    }
                });
    }


    // 7. reduce() 与 scan() 操作符的作用也是将发送数据以一定逻辑聚合起来，这两个的区别在于 scan() 每处理一次
    //    数据就会将事件发送给观察者，而 reduce() 会将所有数据聚合在一起才会发送事件给观察者。
    public void reduce() {
        // 只会发送最后一次处理结果的数据
        Observable.just(0, 1, 2, 3)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        int res = integer + integer2;
                        return res;
                    }
                });

    }

    // 8. collect() 将数据收集到数据结构当中。
    public void collect() {
        Observable.just(1, 2, 3, 4)
                .collect(new Callable<ArrayList<Integer>>() {
                             @Override
                             public ArrayList<Integer> call() throws Exception {
                                 return new ArrayList<>();
                             }
                         },
                        new BiConsumer<ArrayList<Integer>, Integer>() {
                            @Override
                            public void accept(ArrayList<Integer> integers, Integer integer) throws Exception {
                                integers.add(integer);
                            }
                        });
    }


    // 9. startWith() & startWithArray() 在发送事件之前追加事件，startWith() 追加一个事件，startWithArray() 可以追加多个事件。追加的事件会先发出。
    public void startWith() {
        Observable.just(5, 6, 7)
                .startWithArray(2, 3, 4)
                .startWith(1);
    }


    // 10. count() 返回被观察者发送事件的数量
    public void count() {
        Observable.just(1, 2, 3)
                .count();
    }


    // --------------------------------------------------------------------------------------------
    // --------------------------------------功能操作符---------------------------------------------

    // 1. delay() 延迟一段事件发送事件。
    public void delay() {
        Observable.just(1, 2, 3)
                .delay(2, TimeUnit.SECONDS);
    }

    // 2. doOnEach() Observable 每发送一件事件之前都会先回调这个方法
    public void doOnEach() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                //      e.onError(new NumberFormatException());
                e.onComplete();
            }
        }).doOnEach(new Consumer<Notification<Integer>>() {
            @Override
            public void accept(Notification<Integer> integerNotification) throws Exception {

            }
        });
    }

    // 3. doOnNext() Observable 每发送 onNext() 之前都会先回调这个方法
    public void doOnNext() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
            }
        });
    }

    // 4. doAfterNext() Observable 每发送 onNext() 之后都会回调这个方法。
    public void doAfterNext() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        });
    }

    // 5. doOnComplete() Observable 每发送 onComplete() 之前都会回调这个方法。
    public void doOOnComplete() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    // 6. doOnError() Observable 每发送 onError() 之前都会回调这个方法。
    public void doOnError() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new NullPointerException());
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    // 7. doOnSubscribe() Observable 每发送 onSubscribe() 之前都会回调这个方法
    public void doOnSubscribe() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {

            }
        });
    }

    // 8. doOnDispose() 当调用 Disposable 的 dispose() 之后回调该方法。
    public void doOnDispose() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    // 9. doOnLifecycle() 在回调 onSubscribe 之前回调该方法的第一个参数的回调方法，可以使用该回调方法决定是否取消订阅。
    public void doOnLifecycle() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).doOnLifecycle(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {

            }
        }, new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    // 10. doOnTerminate() & doAfterTerminate() doOnTerminate 是在 onError 或者 onComplete 发送之前回调
    //     ，而 doAfterTerminate 则是 onError 或者 onComplete 发送之后回调。
    public void doOnTerminate() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
//      e.onError(new NullPointerException());
                e.onComplete();
            }
        }).doOnTerminate(new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    // 11. doFinally() 在所有事件发送完毕之后回调该方法
    //     如果取消订阅之后 doAfterTerminate() 就不会被回调，而 doFinally() 无论怎么样都会被回调，且都会在事件序列的最后。
    public void doFinally() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    // 12. onErrorReturn() 当接受到一个 onError() 事件之后回调，返回的值会回调 onNext() 方法，并正常结束该事件序列
    public void onErrorReturn() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new NullPointerException());
            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                return 404;
            }
        });
    }

    // 13. onErrorResumeNext() 当接收到 onError() 事件时，返回一个新的 Observable，并正常结束事件序列
    public void onErrorResumeNext() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new NullPointerException());
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                return Observable.just(4, 5, 6);
            }
        });
    }

    // 14. onExceptionResumeNext() 与 onErrorResumeNext() 作用基本一致，但是这个方法只能捕捉 Exception。
    public void onExceptionResumeNext() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                // 如果使用下面这个注释代码则不会执行onNext(333);
//                e.onError(new Error("404"));
                e.onError(new Exception("404"));
            }
        }).onExceptionResumeNext(new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? super Integer> observer) {
                observer.onNext(333);
                observer.onComplete();
            }
        });
    }

    // 15. retry() 如果出现错误事件，则会重新发送所有事件序列。times 是代表重新发的次数
    public void retry() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Exception("404"));
            }
        }).retry(2);
    }

    // 16. retryUntil() 出现错误事件之后，可以通过此方法判断是否继续发送事件
    public void retryUntil() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Exception("404"));
            }
        }).retryUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return false;
            }
        });
    }

    // 17. retryWhen() 当被观察者接收到异常或者错误事件时会回调该方法，这个方法会返回一个新的被观察者。如果返回
    //     的被观察者发送 Error 事件则之前的被观察者不会继续发送事件，如果发送正常事件则之前的被观察者会继续不断重试发送事件。
    public void retryWhen() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("chan");
                e.onNext("ze");
                e.onNext("de");
                e.onError(new Exception("404"));
                e.onNext("haha");
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        if (!throwable.toString().equals("java.lang.Exception: 404")) {
                            return Observable.just("可以忽略的异常");
                        } else {
                            return Observable.error(new Throwable("终止啦"));
                        }
                    }
                });
            }
        });
    }

    // 18. repeat() 重复发送被观察者的事件，times 为发送次数。
    public void repeat() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).repeat(2);
    }

    // 19. repeatWhen() 这个方法可以会返回一个新的被观察者设定一定逻辑来决定是否重复发送事件
    public void repeatWhen() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                return Observable.empty();
                //  return Observable.error(new Exception("404"));
                //  return Observable.just(4); null;
            }
        });
    }

    // 20. subscribeOn() 指定被观察者的线程，要注意的时，如果多次调用此方法，只有第一次有效
    public void subscribeOn() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {

                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
    }

    // 21. observeOn() 指定观察者的线程，每指定一次就会生效一次。
    public void observeOn() {
        Observable.just(1, 2, 3)
                .observeOn(Schedulers.newThread())
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        return Observable.just("chan" + integer);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------过滤操作符---------------------------------------------

    // 1. filter() 通过一定逻辑来过滤被观察者发送的事件，如果返回 true 则会发送事件，否则不会发送
    public void filter() {
        Observable.just(1, 2, 3)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 2;
                    }
                });
    }

    // 2. ofType() 可以过滤不符合该类型事件
    public void ofType() {
        Observable.just(1, 2, 3, "chan", "zhide")
                .ofType(Integer.class);
    }

    // 3. skip() 跳过正序某些事件，count 代表跳过事件的数量
    public void skip() {
        Observable.just(1, 2, 3)
                .skip(2);
    }

    // 4. distinct() 过滤事件序列中的重复事件。
    public void distinct() {
        Observable.just(1, 2, 3, 3, 2, 1)
                .distinct();
    }

    // 5. distinctUntilChanged() 过滤掉连续重复的事件
    public void distinctUntilChanged() {
        Observable.just(1, 2, 3, 3, 2, 1)
                .distinctUntilChanged();
    }

    // 6. take() 控制观察者接收的事件的数量。
    public void take() {
        Observable.just(1, 2, 3, 4, 5)
                .take(3);
    }

    // 7. debounce() 果两件事件发送的时间间隔小于设定的时间间隔则前一件事件就不会发送给观察者。
    public void debounce() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                Thread.sleep(900);
                e.onNext(2);
            }
        }).debounce(1, TimeUnit.SECONDS);
    }

    // 8. firstElement() && lastElement() firstElement() 取事件序列的第一个元素，lastElement() 取事件序列的最后一个元素
    public void firstElement() {
        Observable.just(1, 2, 3, 4)
                .firstElement();
        Observable.just(1, 2, 3, 4)
                .lastElement();
    }

    // 9. elementAt() & elementAtOrError() elementAt() 可以指定取出事件序列中事件，但是输入的 index 超出事
    //    件序列的总数的话就不会出现任何结果。这种情况下，你想发出异常信息的话就用 elementAtOrError()
    public void elementAt() {
        Observable.just(1, 2, 3, 4)
                .elementAt(0);
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------条件操作符---------------------------------------------

    // 1. all() 判断事件序列是否全部满足某个事件，如果都满足则返回 true，反之则返回 false
    public void all() {
        Observable.just(1, 2, 3, 4)
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 5;
                    }
                });
    }

    // 2. takeWhile() 可以设置条件，当某个数据满足条件时就会发送该数据，反之则不发送。
    public void takeWhile() {
        Observable.just(1, 2, 3, 4)
                .takeWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 3;
                    }
                });
    }

    // 3. skipWhile() 可以设置条件，当某个数据满足条件时不发送该数据，反之则发送
    public void skipWhile() {
        Observable.just(1, 2, 3, 4)
                .skipWhile(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer < 3;
                    }
                });
    }

    // 4. takeUntil() 可以设置条件，当事件满足此条件时，下一次的事件就不会被发送了
    public void takeUntil() {
        Observable.just(1, 2, 3, 4, 5, 6)
                .takeUntil(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 3;
                    }
                });
    }

    // 5. skipUntil() 当 skipUntil() 中的 Observable 发送事件了，原来的 Observable 才会发送事件给观察者
    public void skipUntil() {
        Observable.intervalRange(1, 5, 0, 1, TimeUnit.SECONDS)
                .skipUntil(Observable.intervalRange(6, 5, 3, 1, TimeUnit.SECONDS));
    }

    // 6. sequenceEqual() 判断两个 Observable 发送的事件是否相同
    public void sequenceEqual() {
        Observable.sequenceEqual(Observable.just(1, 2, 3),
                Observable.just(1, 2, 3));
    }

    // 7. contains() 判断事件序列中是否含有某个元素，如果有则返回 true，如果没有则返回 false
    public void contains() {
        Observable.just(1, 2, 3)
                .contains(3);
    }

    // 8. isEmpty() 判断事件序列是否为空。
    public void isEmpty() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onComplete();
            }
        }).isEmpty();
    }

    // 9. amb() amb() 要传入一个 Observable 集合，但是只会发送最先发送事件的 Observable 中的事件，其余 Observable 将会被丢弃
    public void amb() {
        ArrayList < Observable < Long >> list = new ArrayList < > ();

        list.add(Observable.intervalRange(1, 5, 2, 1, TimeUnit.SECONDS));
        list.add(Observable.intervalRange(6, 5, 0, 1, TimeUnit.SECONDS));

        Observable.amb(list);
    }

    // 10. defaultIfEmpty() 如果观察者只发送一个 onComplete() 事件，则可以利用这个方法发送一个值
    public void defaultIfEmpty() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onComplete();
            }
        }).defaultIfEmpty(666);
    }

    public class Person {

        private String name;
        private List<Plan> planList;

        public Person(String name, List<Plan> planList) {
            this.name = name;
            this.planList = planList;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Plan> getPlanList() {
            return planList;
        }

        public void setPlanList(List<Plan> planList) {
            this.planList = planList;
        }

    }


    public class Plan {

        private String time;
        private String content;
        private List<String> actionList = new ArrayList<>();

        public Plan(String time, String content) {
            this.time = time;
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getActionList() {
            return actionList;
        }

        public void setActionList(List<String> actionList) {
            this.actionList = actionList;
        }
    }
}
