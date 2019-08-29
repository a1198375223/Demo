package com.example.androidxdemo;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String TAG = "ExampleUnitTest";

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
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
                System.out.println("onFailure: " +  e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: response: body=" + response.body());
                System.out.println("onResponse: response: body=" + response.body());
                Headers headers = response.headers();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < headers.size(); i++) {
                    builder.append(headers.name(i)).append(":").append(headers.value(i)).append("\n");
                }
                Log.d(TAG, "onResponse: headers:\n" + builder.toString());
                System.out.println("onResponse: headers:\n" + builder.toString());
            }
        });
    }
}