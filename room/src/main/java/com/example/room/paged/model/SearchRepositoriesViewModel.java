package com.example.room.paged.model;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.room.mvp.BaseViewModel;
import com.example.room.paged.data.GithubRepository;

import java.util.List;

public class SearchRepositoriesViewModel extends BaseViewModel {
    private static final String TAG = "SearchRepositoriesViewM";
    private final int  VISIBLE_THRESHOLD = 5;
    private GithubRepository repository;

    public SearchRepositoriesViewModel(GithubRepository repository) {
        this.repository = repository;
    }

    private MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    private LiveData<RepoSearchResult> repoResult = Transformations.map(queryLiveData, new Function<String, RepoSearchResult>() {
        @Override
        public RepoSearchResult apply(String input) {
            Log.d(TAG, "repoResult request data");
            return repository.search(input);
        }
    });

    private LiveData<List<Repo>> repos = Transformations.switchMap(repoResult, RepoSearchResult::getData);

    private LiveData<String> networkErrors = Transformations.switchMap(repoResult, RepoSearchResult::getNetworkErrors);

    public void searchRepo(String queryString) {
        queryLiveData.postValue(queryString);
    }


    public void listScrolled(int visibleItemCount, int lastVisibleItemPosition, int totalItemCount) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            String immutableQuery = lastQueryValue();
            if (immutableQuery != null) {
                repository.requestMore(immutableQuery);
            }
        }
    }


    public String lastQueryValue() {
        return queryLiveData.getValue();
    }

    public GithubRepository getRepository() {
        return repository;
    }

    public void setRepository(GithubRepository repository) {
        this.repository = repository;
    }

    public MutableLiveData<String> getQueryLiveData() {
        return queryLiveData;
    }

    public void setQueryLiveData(MutableLiveData<String> queryLiveData) {
        this.queryLiveData = queryLiveData;
    }

    public LiveData<RepoSearchResult> getRepoResult() {
        return repoResult;
    }

    public void setRepoResult(LiveData<RepoSearchResult> repoResult) {
        this.repoResult = repoResult;
    }

    public LiveData<List<Repo>> getRepos() {
        return repos;
    }

    public void setRepos(LiveData<List<Repo>> repos) {
        this.repos = repos;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    public void setNetworkErrors(LiveData<String> networkErrors) {
        this.networkErrors = networkErrors;
    }
}
