package com.example.androidxdemo.activity.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidxdemo.R;

public class SelectContactActivity extends Activity {
    private static final String TAG = "SelectContactActivity";
    public static final String ACTION_SELECT_CONTACT = "com.example.androidxdemo.intent.action.SELECT_CONTACT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        Intent intent = getIntent();
        if (!ACTION_SELECT_CONTACT.equals(intent.getAction())) {
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(mContactAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private final RecyclerView.Adapter mContactAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contat_item, parent, false);
            return new ContactViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ContactViewHolder) {
                Contact contact = Contact.CONTACTS[position];
                ((TextView) holder.itemView).setText(contact.getName());
                ((TextView) holder.itemView).setCompoundDrawablesRelativeWithIntrinsicBounds(contact.getIcon(), 0, 0, 0);
                holder.itemView.setOnClickListener(view -> {
                    Intent data = new Intent();
                    data.putExtra(Contact.ID, position);
                    setResult(RESULT_OK, data);
                    finish();
                });
            }
        }

        @Override
        public int getItemCount() {
            return Contact.CONTACTS.length;
        }
    };


    private static class ContactViewHolder extends RecyclerView.ViewHolder {
        public ContactViewHolder(@NonNull TextView itemView) {
            super(itemView);
        }
    }
}
