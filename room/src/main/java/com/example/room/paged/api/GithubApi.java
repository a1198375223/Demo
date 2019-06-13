package com.example.room.paged.api;

import android.util.Log;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubApi {
    private static final String TAG = "GithubApi";
    public static final String BASE_URL = "https://api.github.com/";
    private final String IN_QUALIFIER = "in:name,description";

    private GithubService mService;

    public GithubApi() {
        mService = create();
    }

    // 创建一个retrofit2
    private GithubService create() {
        // 拦截器
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor(s -> {
            try {
                String message = URLDecoder.decode(s, "utf-8");
                Log.e(TAG, "create: message=" + message);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "create: message=" + s + " throwable=", e);
            }
        });
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubService.class);
    }

    public Call<RepoSearchResponse> searchRepos(String query, int page, int itemsPerPage) {
        Log.d(TAG, "search from server query=" + query + " page=" + page + " itemsPerPage=" + itemsPerPage);
        String apiQuery = query + IN_QUALIFIER;
        return mService.searchRepos(apiQuery, page, itemsPerPage);
    }
}
