package com.example.dialog;

public enum  ListType {
    REGULAR, SINGLE, MULTI;

    public static int getLayoutForType(ListType type) {
        switch (type) {
            case REGULAR:
                return R.layout.md_listitem;
            case SINGLE:
                return R.layout.md_listitem_singlechoice;
            case MULTI:
                return R.layout.md_listitem_multichoice;
            default:
                throw new IllegalArgumentException("Not a valid list type");
        }
    }
}
