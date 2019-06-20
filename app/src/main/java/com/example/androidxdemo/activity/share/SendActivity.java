package com.example.androidxdemo.activity.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.Toasty;

public class SendActivity extends Activity {
    private static final String TAG = "SendActivity";
    private static final int REQUEST_SELECT_CONTACT = 1;

    private TextView mName;
    private TextView mBody;

    private String body;
    private int mContactId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        setTitle("发送一个消息");

        mName = findViewById(R.id.contact_name);
        mBody = findViewById(R.id.message_body);

        boolean handled = handleIntent(getIntent());
        if (!handled) {
            finish();
            return;
        }

        findViewById(R.id.send).setOnClickListener(view -> {
            Toasty.showSuccess("发送消息{" + body + " to " + Contact.byId(mContactId).getName() + "}成功!");
            finish();
        });

        prepareUi();

        if (mContactId == Contact.INVALID_ID) {
            selectContact();
        }
    }

    private void selectContact() {
        Intent intent = new Intent(this, SelectContactActivity.class);
        intent.setAction(SelectContactActivity.ACTION_SELECT_CONTACT);
        startActivityForResult(intent, REQUEST_SELECT_CONTACT);
    }

    private boolean handleIntent(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction())
                && "text/plain".equals(intent.getType())) {
            body = intent.getStringExtra(Intent.EXTRA_TEXT);
            mContactId = Contact.INVALID_ID;
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_CONTACT:
                if (data == null) {
                    Toasty.showError("result intent data == null.");
                    return;
                }
                if (resultCode == RESULT_OK) {
                    mContactId = data.getIntExtra(Contact.ID, Contact.INVALID_ID);
                }

                if (mContactId == Contact.INVALID_ID) {
                    finish();
                    return;
                }
                prepareUi();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void prepareUi() {
        if (mContactId != Contact.INVALID_ID) {
            Contact contact = Contact.byId(mContactId);
            mName.setText(contact.getName());
            mName.setCompoundDrawablesRelativeWithIntrinsicBounds(contact.getIcon(), 0, 0, 0);
        }
        mBody.setText(body);
    }
}


