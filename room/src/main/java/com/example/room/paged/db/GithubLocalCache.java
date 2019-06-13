package com.example.room.paged.db;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.commonlibrary.common.CommonCallback;
import com.example.commonlibrary.utils.ThreadPool;
import com.example.room.paged.model.Repo;

import java.util.Arrays;
import java.util.List;

// 使用这个类来对数据库进行操作
public class GithubLocalCache {
    private static final String TAG = "GithubLocalCache";

    private RepoDao repoDao;

    public GithubLocalCache(RepoDao repoDao) {
        this.repoDao = repoDao;
    }

    public void insert(List<Repo> repos, CommonCallback callback) {
        ThreadPool.runOnIOPool(() -> {
            Log.d(TAG, "insert: repos size=" + repos.size() + " repos=" + repos.toString());
//            repoDao.insertRepos((Repo[]) repos.toArray());
            Long[] result = repoDao.insert(repos);
            Log.d(TAG, "insert success result=" + Arrays.toString(result));
            callback.callback();
        });
    }

    // 需要在非ui线程调用
    public LiveData<List<Repo>> reposByName(String name) {
        String query = name.replace(' ', '%');
        query = "%" + query + "%";
        Log.d(TAG, "reposByName: query=" + query);
        return repoDao.reposAll(query);
    }
}
