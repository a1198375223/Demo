package com.example.androidxdemo.mianshi;


import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * 总结
 * 1. okhttp3内部有一个RealCall类, 保存了OkHttpClient与Request的信息
 * 2. OkHttpClient中有一个Dispatcher对象, 封装了一线程池, 内部维护了3个队列
 *    a. 在正执行的异步操作队列
 *    b. 正在执行的同步操作队列
 *    c. 准备执行的请求队列
 *
 * 3. 当调用Call.execute()作同步请求的时候, 将它添加到正在执行的同步操作队列中去, 然后直接调用RealCall.executeOn()方法
 * 4. 当调用Call.enqueue()作异步请求的时候, 将它添加到准备队列, 并且试图在线程池中执行它
 * 5. 无论是调用同步还是异步请求都最终调用getResponseWithInterceptorChain(), 真正的耗时操作就在这个方法中. 该方法中会创建
 *    多个Interceptor拦截器来处理网络请求, 通过链式调用的方式来完成网络请求. 调用顺序如下：
 *    a. client.interceptor             : 用户自定义的拦截器, 可以使用LoggerInterceptor拦截器来查看日志
 *    b. RetryAndFollowUpInterceptor    : 实现重试、跟踪
 *    c. BridgeInterceptor              : 将用户构建的 Request 请求转换为能够进行网络访问的请求
 *    d. CacheInterceptor               : 实现缓存功能的拦截器
 *    e. ConnectInterceptor             : 打开一个面向指定服务器的连接，并且执行下一个拦截器。
 *    f. client.networkInterceptors     : 用户自定义的拦截器, 可以使用LoggerInterceptor拦截器来查看日志
 *    g. CallServerInterceptor          : 这是 Okhttp 库中拦截器链的最后一个拦截器，也是这个拦截器区具体发起请求和获取响应。
 */
public class OkHttp3Detail {
    private static final String TAG = "OkHttp3Detail";

    // 同步做sync请求
    public void doGetSync(String url) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        Log.d(TAG, "doGetSync: response: " + response.body());
    }


    // 异步get请求
    public void doGetAsync(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: response: " + response.body());
            }
        });
    }


    // 异步post请求 body是string
    public void postStringTest() {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        String requestBody = "hello github";
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(requestBody, mediaType))
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: response: body=" + response.body());
                Headers headers = response.headers();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < headers.size(); i++) {
                    builder.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
                }
                Log.d(TAG, "onResponse: headers:\n" + builder.toString());
            }
        });
    }


    // post请求byte
    public void postByteTest() throws IOException {
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
        Log.d(TAG, "postByteTest: " + response.protocol() + " " + response.code() + " " + response.message());
        Headers headers = response.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        Log.d(TAG, "postByteTest: headers:\n{}" +  sb.toString());
        Log.d(TAG, "postByteTest: onResponse: " + Objects.requireNonNull(response.body()).string());
    }

    public void postFileTest() throws IOException {
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        File file = new File("/home/mi/test.md");
        Request request = new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(file, mediaType))
                .build();
        Response response = client.newCall(request).execute();
        Log.d(TAG, "postByteTest: " + response.protocol() + " " + response.code() + " " + response.message());
        Headers headers = response.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        Log.d(TAG, "postByteTest: headers:\n{}" +  sb.toString());
        Log.d(TAG, "postByteTest: onResponse: " + Objects.requireNonNull(response.body()).string());
    }

    public void postFormTest() throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("search", "java")
                .build();

        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        Log.d(TAG, "postByteTest: " + response.protocol() + " " + response.code() + " " + response.message());
        Headers headers = response.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        Log.d(TAG, "postByteTest: headers:\n{}" +  sb.toString());
        Log.d(TAG, "postByteTest: onResponse: " + Objects.requireNonNull(response.body()).string());
    }


    public void postMutilPartFormTest() throws IOException {
        MediaType mediaType = MediaType.parse("image/png");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("extra", "extra args")
                .addFormDataPart("file", "logo.png", RequestBody.create(new File("/home/mi/timg_108.png"), mediaType))
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:8888/base-platform/upload/uploadFile")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        Log.d(TAG, "postByteTest: " + response.protocol() + " " + response.code() + " " + response.message());
        Headers headers = response.headers();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            sb.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
        }
        Log.d(TAG, "postByteTest: headers:\n{}" +  sb.toString());
        Log.d(TAG, "postByteTest: onResponse: " + Objects.requireNonNull(response.body()).string());
    }
}
