package com.example.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.room.dao.UserDao;
import com.example.room.dao.database.AppDatabase;
import com.example.room.paged.db.RepoDao;

public class RoomUtils {
    private static final String TAG = "RoomUtils";
    private static AppDatabase sDatabase;

    // 放在application中初始化, 就可以使用这个工具类来访问数据库了
    static void init(Context context) {
        sDatabase = Room
                .databaseBuilder(context, AppDatabase.class, "database.pb")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                    }
                })
//                .addMigrations()
//                .allowMainThreadQueries()
//                .enableMultiInstanceInvalidation()
//                .fallbackToDestructiveMigration()
//                .fallbackToDestructiveMigrationFrom()
//                .fallbackToDestructiveMigrationOnDowngrade()
//                .openHelperFactory()
//                .setJournalMode()
//                .setQueryExecutor()
//                .setTransactionExecutor()
                .build();
    }

    // 访问User数据库
    public static UserDao getUserDao() {
        return sDatabase.userDao();
    }

    // 访问Repo数据库
    public static RepoDao getRepoDao() {
        return sDatabase.getRepoDao();
    }

    static void destroy() {
        if (sDatabase != null) {
            sDatabase = null;
        }
    }
}
