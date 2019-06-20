package com.example.androidxdemo.activity.share;

import com.example.androidxdemo.R;

public class Contact {
    public static final String ID = "contact_id";
    public static final int INVALID_ID = -1;


    private String name;

    public static final Contact[] CONTACTS = {
            new Contact("zza"),
            new Contact("lhy"),
            new Contact("jcl"),
            new Contact("zys"),
            new Contact("swl"),
            new Contact("dyx")
    };

    public Contact(String name) {
        this.name = name;
    }

    public static Contact byId(int id) {
        return CONTACTS[id];
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return R.mipmap.ic_launcher;
    }
}
