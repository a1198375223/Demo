package com.example.androidxdemo.activity.share;


import android.content.Context;
import android.content.Intent;

import androidx.core.app.Person;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SharingShortcutsManager {
    private static final String TAG = "SharingShortcutsManager";
    private static final int MAX_SHORTCUTS = 4;
    private static final String CATEGORY_TEXT_SHARE_TARGET = "com.example.androidxdemo.activity.share.category.TEXT_SHARE_TARGET";

    public void pushDirectShareTargets(Context context) {
        ArrayList<ShortcutInfoCompat> shortcuts = new ArrayList<>();

        Set<String> contactCategories = new HashSet<>();
        contactCategories.add(CATEGORY_TEXT_SHARE_TARGET);

        for (int id = 0; id < MAX_SHORTCUTS; id++) {
            Contact contact = Contact.byId(id);

            Intent shortcutIntent = new Intent(Intent.ACTION_DEFAULT);

            shortcuts.add(new ShortcutInfoCompat.Builder(context, Integer.toString(id))
                    .setShortLabel(contact.getName())
                    .setIcon(IconCompat.createWithResource(context, contact.getIcon()))
                    .setIntent(shortcutIntent)
                    // 通过系统缓存
                    .setLongLived()
                    .setCategories(contactCategories)
                    .setPerson(new Person.Builder()
                            .setName(contact.getName())
                            .build())
                    .build());
        }
        ShortcutManagerCompat.addDynamicShortcuts(context, shortcuts);
    }

    public void removeAllDirectShareTargets(Context context) {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context);
    }
}
