package com.example.androidxdemo.activity.test.visibility;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.google.gson.Gson;

public class VisibleActivity extends AppCompatActivity {
    private static final String TAG = "VisibleActivity";

    private String gson = "0{\"code\":4,\"data\":{}}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visible);

        ((VisibilityLinearLayout) findViewById(R.id.container)).setOnVisibilityChangeListener((changedView, visibility) -> Log.d(TAG, "onVisibilityChange: view=" + changedView + " view id=" + changedView.getId() + " visibility=" + visibility));
        int beginIndex = gson.indexOf("{");
        int lastindex = gson.lastIndexOf("}");
        TaskData json = new Gson().fromJson(gson.substring(beginIndex, lastindex + 1), TaskData.class);
        ((TextView) findViewById(R.id.child1)).setText(json.toString());

        findViewById(R.id.bn).setOnClickListener(view -> {
            findViewById(R.id.group).setVisibility(View.VISIBLE);
        });

        findViewById(R.id.gone).setOnClickListener(view -> {
            findViewById(R.id.group).setVisibility(View.GONE);
        });
    }
}
