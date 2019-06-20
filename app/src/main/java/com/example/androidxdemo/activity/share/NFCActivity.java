package com.example.androidxdemo.activity.share;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.Toasty;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;


/**
 * NFC介绍
 * NDEF数据, NFC主要通过这个NDEF数据进行通信的.主要支持的操作如下：
 * 1. 从NFC标签读取NDEF格式的数据
 * 2. 向NFC标签写入NDEF格式的数据
 * 3. 通过Android Beam技术将NDEF数据发送到另一台NFC设备
 * 主要涉及到的两个类
 * 1. NdefMessage: 用来描述NDEF格式的信息，实际上我们写入NDC标签的就是NdefMessage对象
 * 2. NdefRecord: 用来描述NDEF信息的一个信息段, 一个NdefMessage可能包括一个或者多个NdefRecord
 *
 *
 * ACTION_NDEF_DISCOVERED : 最先被filter筛选的action
 * ACTION_TECH_DISCOVERED : 第二
 * ACTION_TAG_DISCOVERED  : 最后
 *
 * 如果应用是通过ACTION_TAG_DISCOVERED启动的, 那么我们可以从Intent中获取被扫描的NFC的TAG信息
 * EXTRA_TAG: NFC的tag标记
 * EXTRA_NDEF_MESSAGES: NDEF数组Messages 从tag解析而来
 * EXTRA_ID: low-level的tag
 *
 *
 * NDEF records
 * TNF_ABSOLUTE_URI
 * TNF_MIME_MEDIA
 * TNF_WELL_KNOWN with RTD_TEXT
 * TNF_WELL_KNOWN with RTD_URI
 * TNF_EXTERNAL_TYPE
 *
 *
 * 使用Android Beam之前先要调用下面两个的其中一个方法
 * setNdefPushMessage()
 * setNdefPushMessageCallback()
 * 一个Activity一次只能push一个NdefMessage, 如果上面两个方法都存在的话, 则优先调用setNdefPushMessageCallback()这个方法
 */
public class NFCActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
    private static final String TAG = "NFCActivity";
    private String mPackageName = "com.android.mms"; // 短信

    private TextView mTv;
    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private IntentFilter ndef;
    private String[][] techListsArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mTv = findViewById(R.id.nfc_tv);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Log.e(TAG, "error nfc adapter == null");
            Toasty.showError("error nfc adapter == null");
            finish();
            return;
        }

        mNfcAdapter.setNdefPushMessageCallback(this, this);
        
        // 创建一个PendingIntent
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        intentFilters = new IntentFilter[]{ndef};

        techListsArray = new String[][] {new String[] {
                NfcF.class.getName(),
                NfcA.class.getName(),
                NfcB.class.getName(),
                NfcV.class.getName(),
                Ndef.class.getName(),
                IsoDep.class.getName(),
                NdefFormatable.class.getName(),
                MifareClassic.class.getName(),
                MifareUltralight.class.getName()
        }};
    }

    /**
     * onNewIntent的触发的时机 当前实例存在的情况下又启动了这个Activity
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
        // 将intent交给onResume处理
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techListsArray);
        if (getIntent() != null) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Log.d(TAG, "action is action_ndef_discovered tag=" + tag.toString());
                Parcelable[] rawMessages = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMessages != null) {
                    // 创建NdefMessage对象来获取数据
                    NdefMessage[] messages = new NdefMessage[rawMessages.length];
                    for (int i = 0; i < rawMessages.length; i++) {
                        messages[i] = (NdefMessage) rawMessages[i];
                    }
                    Log.d(TAG, "Ndef message=" + Arrays.toString(messages));
                    mTv.setText(new StringBuilder("nfc 数据=").append(Arrays.toString(messages)));
                } else {
                    Log.d(TAG, "action action_ndef_discovered message is null");
                }
            }
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    private void writeNFCTag(Tag tag) {
        if (tag == null) {
            return;
        }
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{
                NdefRecord.createApplicationRecord(mPackageName)
        });

        int size = ndefMessage.toByteArray().length;
        Ndef ndef = Ndef.get(tag);

        try {
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    return;
                }

                if (ndef.getMaxSize() < size) {
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                Toasty.showSuccess("写入成功");
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    format.connect();
                    format.format(ndefMessage);
                    Toasty.showSuccess("写入成功");
                } else {
                    Toasty.showError("写入失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 创建TNF_WELL_KNOWN with RTD_TEXT方法
     * @param payload
     * @param locale
     * @param encodeInUtf8
     * @return
     */
    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
    }

    // 创建NdefMessage
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.d(TAG, "createNdefMessage: event=" + event.toString());
        String text = "Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis();
        NdefMessage msg = new NdefMessage(new NdefRecord[]{
                NdefRecord.createMime("application/vnd.com.example.android.beam", text.getBytes())
        });
        return msg;
    }
}
