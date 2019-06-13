package com.example.room.paged.ui;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room.R;
import com.example.room.paged.model.Repo;

public class RepoAdapter extends ListAdapter<Repo, RecyclerView.ViewHolder> {
    private static final String TAG = "RepoAdapter";

    protected RepoAdapter(@NonNull DiffUtil.ItemCallback<Repo> diffCallback) {
        super(diffCallback);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RepoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_repo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RepoViewHolder) {
            Repo item = getItem(position);
            if (item != null) {
                ((RepoViewHolder) holder).bind(position, item);
            }
        }
    }

    class RepoViewHolder extends RecyclerView.ViewHolder {
        private int position;
        private Repo repo;
        private TextView name, description, stars, language, forks;

        public RepoViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.repo_name);
            description = itemView.findViewById(R.id.repo_description);
            stars = itemView.findViewById(R.id.repo_stars);
            language = itemView.findViewById(R.id.repo_language);
            forks = itemView.findViewById(R.id.repo_forks);

            itemView.setOnClickListener(view -> {
                if (repo != null && repo.getUrl() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(repo.getUrl()));
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void bind(int position, Repo repo) {
            this.position = position;

            if (repo == null) {
                name.setText(itemView.getContext().getResources().getString(R.string.loading));
                description.setVisibility(View.GONE);
                language.setVisibility(View.GONE);
                stars.setText(itemView.getContext().getResources().getString(R.string.unknown));
                forks.setText(itemView.getContext().getResources().getString(R.string.unknown));
            } else {
                showRepoData(repo);
            }
        }

        private void showRepoData(Repo repo) {
            this.repo = repo;
            name.setText(repo.getFullName());

            if (repo.getDescription() != null) {
                description.setText(repo.getDescription());
                description.setVisibility(View.VISIBLE);
            } else {
                description.setVisibility(View.GONE);
            }

            stars.setText(String.valueOf(repo.getStars()));
            forks.setText(String.valueOf(repo.getForks()));

            if (repo.getLanguage() != null && !TextUtils.isEmpty(repo.getLanguage())) {
                language.setText(itemView.getContext().getResources().getString(R.string.language, repo.getLanguage()));
                language.setVisibility(View.VISIBLE);
            } else {
                language.setVisibility(View.GONE);
            }
        }
    }
}
