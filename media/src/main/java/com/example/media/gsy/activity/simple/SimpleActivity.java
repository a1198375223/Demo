package com.example.media.gsy.activity.simple;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.media.R;
import com.example.media.R2;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SimpleActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @OnClick({R2.id.simple_list_1, R2.id.simple_list_2, R2.id.simple_detail_1, R2.id.simple_detail_2, R2.id.simple_player})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.simple_player) {
            startActivity(new Intent(this, SimplePlayerActivity.class));
        } else if (i == R.id.simple_list_1) {
//            startActivity(new Intent(this, SimpleListVideoActivityMode1.class));
        } else if (i == R.id.simple_list_2) {
//            startActivity(new Intent(this, SimpleListVideoActivityMode2.class));
        } else if (i == R.id.simple_detail_1) {
//            startActivity(new Intent(this, SimpleDetailActivityMode1.class));
        } else if (i == R.id.simple_detail_2) {
//            startActivity(new Intent(this, SimpleDetailActivityMode2.class));
        }
    }
}
