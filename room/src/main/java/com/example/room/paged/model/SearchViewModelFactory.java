package com.example.room.paged.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.room.paged.data.GithubRepository;

public class SearchViewModelFactory implements ViewModelProvider.Factory {
    private GithubRepository repository;

    public SearchViewModelFactory(GithubRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchRepositoriesViewModel.class)) {
            return (T) new SearchRepositoriesViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
