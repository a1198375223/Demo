package com.example.androidxdemo.activity.receiver.net;

import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.androidxdemo.receiver.NetworkReceiver;
import com.example.commonlibrary.utils.NetworkUtils;


public class NetWorkActivity extends AppCompatActivity {
    private NetworkReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text);
        StringBuilder builder = new StringBuilder();
        if (NetworkUtils.isMobile(this)) {
            builder.append("当前网络是数据流量");
        } else if (NetworkUtils.isWifi(this)) {
            builder.append("当前网络是wifi。");
        }

        builder.append("\n获取到的network的名称=").append(NetworkUtils.getNetworkName(this));
        builder.append("\n转化过的network名称=").append(NetworkUtils.corvertNetoworkName(NetworkUtils.corvertActiveNetworkType(this)));
        textView.setText(builder.toString());



        final TextView net = findViewById(R.id.net_state);
        mReceiver = new NetworkReceiver(new NetworkReceiver.OnNetworkConnectedListener() {
            @Override
            public void onNetworkConnected(String name) {
                net.setText(new StringBuilder("now network name is:") .append(name));
            }

            @Override
            public void onNoneNetwork() {
                net.setText("当前没有网络");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
