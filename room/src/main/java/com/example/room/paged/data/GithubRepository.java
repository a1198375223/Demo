package com.example.room.paged.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.room.paged.api.GithubApi;
import com.example.room.paged.api.RepoSearchResponse;
import com.example.room.paged.db.GithubLocalCache;
import com.example.room.paged.model.Repo;
import com.example.room.paged.model.RepoSearchResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GithubRepository {
    private static final String TAG = "GithubRepository";

    private final int NETWORK_PAGE_SIZE = 50;

    private GithubApi service;
    private GithubLocalCache cache;
    private int lastRequestedPage = 1;
    private MutableLiveData<String> networkErrors = new MutableLiveData<>();
    private boolean isRequestInProgress = false;

    public GithubRepository(GithubLocalCache cache) {
        this.service = new GithubApi();
        this.cache = cache;
    }


    public RepoSearchResult search(String query) {
        Log.d(TAG, "search: new query=" + query);

        lastRequestedPage = 1;

        requestAndSaveData(query);

        return new RepoSearchResult(cache.reposByName(query), networkErrors);
    }


    private void requestAndSaveData(String query) {
        if (isRequestInProgress)
            return;

        isRequestInProgress = true;
//        service.searchRepos(query, lastRequestedPage, NETWORK_PAGE_SIZE)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(repoSearchResponse -> {
//                    Log.d(TAG, "onNext: total count=" + repoSearchResponse.getTotal() + " next page=" + repoSearchResponse.getNextPage());
//                    cache.insert(repoSearchResponse.getItems(), () -> {
//                        lastRequestedPage++;
//                        isRequestInProgress = false;
//                    });
//                }, throwable -> {
//                    Log.e(TAG, "onError: ", throwable);
//                    networkErrors.postValue(throwable.toString());
//                    isRequestInProgress = false;
//                });
        service.searchRepos(query, lastRequestedPage, NETWORK_PAGE_SIZE).enqueue(new Callback<RepoSearchResponse>() {
            @Override
            public void onResponse(@NotNull Call<RepoSearchResponse> call, @NotNull Response<RepoSearchResponse> response) {

                if (response.body() != null) {
                    Log.d(TAG, "onResponse: total count=" + response.body().getTotal() + " next page=" + response.body().getNextPage());
                    cache.insert(response.body().getItems(), () -> {
                        lastRequestedPage++;
                        isRequestInProgress = false;
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<RepoSearchResponse> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                networkErrors.postValue(t.toString());
                isRequestInProgress = false;
            }
        });
    }


    public void requestMore(String query) {
        requestAndSaveData(query);
    }
}
