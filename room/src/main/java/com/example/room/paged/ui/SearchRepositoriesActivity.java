package com.example.room.paged.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room.R;
import com.example.room.paged.Injection;
import com.example.room.paged.model.Repo;
import com.example.room.paged.model.SearchRepositoriesViewModel;

public class SearchRepositoriesActivity extends AppCompatActivity {
    private static final String TAG = "SearchRepositoriesActiv";

    private final String LAST_SEARCH_QUERY = "last_search_query";
    private final String DEFAULT_QUERY = "Android";

    private SearchRepositoriesViewModel mViewModel;
    private RepoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyText;
    private EditText mSearchEdit;

    // 通过这个
    private DiffUtil.ItemCallback<Repo> mRepoCallback = new DiffUtil.ItemCallback<Repo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Repo oldItem, @NonNull Repo newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Repo oldItem, @NonNull Repo newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_repositories);

        mViewModel = ViewModelProviders.of(this, new Injection().provideViewModelFactory()).get(SearchRepositoriesViewModel.class);
        mEmptyText = findViewById(R.id.emptyList);
        mSearchEdit = findViewById(R.id.search_repo);

        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.addItemDecoration(decoration);
        setupScrollListener();

        mAdapter = new RepoAdapter(mRepoCallback);
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.getRepos().observe(this, repos -> {
            Log.d(TAG, "repos size=" + repos.size());
            showEmptyList(repos.size() == 0);
            mAdapter.submitList(repos);
        });

        mViewModel.getNetworkErrors().observe(this, s -> Toast.makeText(this, "\uD83D\uDE28 Wooops " + s, Toast.LENGTH_SHORT).show());

        String query = DEFAULT_QUERY;

        if (savedInstanceState != null) {
            query = savedInstanceState.getString(LAST_SEARCH_QUERY);
        }

        mViewModel.searchRepo(query);

        mSearchEdit.setText(query);
        mSearchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput();
                return true;
            }
            return false;
        });

        mSearchEdit.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput();
                return true;
            }
            return false;
        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_SEARCH_QUERY, mViewModel.lastQueryValue());
    }

    private void updateRepoListFromInput() {
        if (!TextUtils.isEmpty(mSearchEdit.getText().toString())) {
            mRecyclerView.scrollToPosition(0);
            mViewModel.searchRepo(mSearchEdit.getText().toString());
            mAdapter.submitList(null);
        }
    }


    private void showEmptyList(boolean show) {
        if (show) {
            mEmptyText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyText.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }



    private void setupScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.getLayoutManager() != null) {
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    int childItemCount = recyclerView.getLayoutManager().getChildCount();
                    int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                    mViewModel.listScrolled(childItemCount, lastVisibleItem, totalItemCount);
                }
            }
        });
    }
}
