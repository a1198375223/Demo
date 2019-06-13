package com.example.room.dao.database;

import com.example.room.dao.UserDao;
import com.example.room.dao.convert.Converters;
import com.example.room.dao.entity.User;
import com.example.room.paged.db.RepoDao;
import com.example.room.paged.model.Repo;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// @Database注解表示这是一个room数据库 entities引用所有的@Entity注解的表, version是当前数据库的版本
/*
获取数据库对象的方法, 注意要遵循单利模式
AppDatabase db = Room.databaseBuilder(getApplicationContext(),
        AppDatabase.class, "database-name").build();
 */
@Database(entities = {
        User.class,
        Repo.class
}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract RepoDao getRepoDao();
}
