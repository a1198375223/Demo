package com.example.room.dao;

import android.database.Cursor;

import com.example.room.dao.entity.User;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

// Dao注解表示这是一个可以对数据库进行操作的类
// 定义一些方法来操作类
// Tips:
// 1. Dao不允许在主线程中访问数据库, 除非在建造者上调用了 allowMainThreadQueries()
// 2. Dao异步查询,返回LiveData或者RxJava Flowable
// 3. 通过make project会自动生成UserDao_Impl.java文件, 是UserDao的实现类
@Dao
public interface UserDao {

    // @Query查询注解, 括号内使用sql语句进行查询
    @Query("SELECT * FROM user")
    List<User> getAll();


    // 在@Query注解中如果需要引用参数的值, 可以是用 :参数名 来获取
    // Tips:
    // 如果希望数据库跟新的时候,动态跟新查询的返回数据,需要使用LiveData来存储查询的数据
    @Query("SELECT * FROM user " +
            "where first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    LiveData<User> findByName(String first, String last);

    // @Query查询还支持返回RxJava2数据
    @Query("SELECT * FROM user WHERE uid = :id LIMIT 1")
    LiveData<User> loadUserById(int id);

    // @Query返回行可以返回一个Cursor对象
    // 不推荐使用
    @Deprecated
    @Query("SELECT * FROM user WHERE first_name LIKE :first LIMIT 1")
    Cursor loadUserByFirstName(String first);


    // @Insert注解表示这是一个insert操作的方法
    // 如果 @Insert 方法只接受到一个参数，它能返回一个 long，表示被插入项的新 rowId。如果参数是一个数组或集合，它应该返回 long[] 或 List。
    // Tips:
    // insert过程可能存在冲突, 可以手动选择处理方式
    // REPLACE  : 替换旧数据,继续执行
    // ROLLBACK : 回滚事务 (弃用)
    // ABORT    : 终止事务
    // FAIL     : 是事务失败 (弃用)
    // IGNORE   : 忽视冲突, 跳过当前插入信息,执行下一个插入
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);


    // single是rx的publisher 一次只发送一个next事件或者error事件
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(User user);



    // @Delete注解表示这是一个delete操作的方法
    // 虽然通常不是必须的，可以让该方法返回一个 int 值，表示从数据库中删除的行数。
    @Delete
    void delete(User... user);

    // completable是rx的publisher 一次只发送一个complete事件或者error事件, 不能发送数据
    @Delete
    Completable delete(User users);


    // @Update注解表示这是一个update操作的方法
    // 虽然通常不是必须的，可以让该方法返回一个 int 值，表示数据库中更新的行数
    @Update
    void update(User... users);

    // maybe是rx的publisher 一次能发射一个数据, 一个完成通知, 或者一条error事件
    @Update
    Maybe<Integer> update(User user);
}
