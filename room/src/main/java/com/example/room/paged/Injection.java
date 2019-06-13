package com.example.room.paged;


import androidx.lifecycle.ViewModelProvider;

import com.example.room.RoomUtils;
import com.example.room.paged.data.GithubRepository;
import com.example.room.paged.db.GithubLocalCache;
import com.example.room.paged.model.SearchViewModelFactory;

public class Injection {

    private GithubLocalCache provideCache() {
        return new GithubLocalCache(RoomUtils.getRepoDao());
    }

    private GithubRepository provideGithubRepository() {
        return new GithubRepository(provideCache());
    }

    public ViewModelProvider.Factory provideViewModelFactory() {
        return new SearchViewModelFactory(provideGithubRepository());
    }
}
