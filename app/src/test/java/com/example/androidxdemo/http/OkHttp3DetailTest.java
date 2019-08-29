package com.example.androidxdemo.http;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;


public class OkHttp3DetailTest {
    private static final String TAG = "OkHttp3DetailTest";

    @Test
    public void postStringTest() throws IOException {
        System.out.println("测试开始");
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        String requestBody = "hello github";
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(requestBody, mediaType))
                .build();

        System.out.println("request=" + request.toString());

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        if (body != null) {
            System.out.println("onResponse: response body=" + body.string());
        }
        System.out.println("onResponse: response: message=" + response.message());
        Headers headers = response.headers();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            builder.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        System.out.println("onResponse: headers:\n" + builder.toString());
        System.out.println("测试结束");
    }


    @Test
    public void postByteTest() throws IOException {
        System.out.println("测试开始");
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType.parse("text/x-markdown; charset=utf-8");
            }

            @Override
            public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                bufferedSink.writeUtf8("你好, github");
            }
        };

        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        System.out.println("postByteTest: " + response.protocol() + " " + response.code() + " " + response.message());
        Headers headers = response.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        System.out.println("postByteTest: headers:\n{}" +  sb.toString());
        System.out.println("postByteTest: onResponse: " + Objects.requireNonNull(response.body()).string());
        System.out.println("测试结束");
    }

    @Test
    public void postFileTest() throws IOException {
        System.out.println("测试开始");
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        File file = new File("/home/mi/test.md");
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(file, mediaType))
                .build();
        Response response = client.newCall(request).execute();
        System.out.println("postByteTest: " + response.protocol() + " " + response.code() + " " + response.message());
        Headers headers = response.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        System.out.println("postByteTest: headers:\n{}" +  sb.toString());
        System.out.println("postByteTest: onResponse: " + Objects.requireNonNull(response.body()).string());
        System.out.println("测试结束");
    }


    @Test
    public void postFormTest() throws IOException {
        System.out.println("测试开始");
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("search", "java")
                .build();

        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("postByteTest: " + response.protocol() + " " + response.code() + " " + response.message());
        Headers headers = response.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        System.out.println("postByteTest: headers:\n{}" +  sb.toString());
        System.out.println("postByteTest: onResponse: " + Objects.requireNonNull(response.body()).string());
        System.out.println("测试结束");
    }
}