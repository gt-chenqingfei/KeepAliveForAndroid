package com.android.keepalive.account;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.android.keepalive.KeepAliveApplication;

public class KeepAliveAccountProvider extends ContentProvider {
    public static final String AUTHORITY = ".account.provider";
    public static final String CONTENT_URI_BASE = "content://";
    public static final String TABLE_NAME = "data";
//    public static final Uri CONTENT_URI =

    public static Uri getContentURI() {
        return Uri.parse(CONTENT_URI_BASE + KeepAliveApplication.getApp().getPackageName() + AUTHORITY + "/" + TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return new String();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
