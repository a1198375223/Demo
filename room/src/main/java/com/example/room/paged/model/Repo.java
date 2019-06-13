package com.example.room.paged.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity(tableName = "repos")
public class Repo {

    public Repo() {}

    @PrimaryKey
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("description")
    private String description;

    @SerializedName("html_url")
    private String url;

    @SerializedName("stargazers_count")
    private int stars;

    @SerializedName("forks_count")
    private int forks;

    @SerializedName("language")
    private String language;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Repo) {
            return TextUtils.equals(((Repo) obj).description, description) &&
                    TextUtils.equals(((Repo) obj).language, language) &&
                    TextUtils.equals(((Repo) obj).fullName, fullName) &&
                    TextUtils.equals(((Repo) obj).name, name) &&
                    TextUtils.equals(((Repo) obj).url, url) &&
                    ((Repo) obj).id == id &&
                    ((Repo) obj).stars == stars &&
                    ((Repo) obj).forks == forks;

        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Repo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", stars=" + stars +
                ", forks=" + forks +
                ", language='" + language + '\'' +
                '}';
    }
}
