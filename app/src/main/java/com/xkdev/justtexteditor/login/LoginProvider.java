package com.xkdev.justtexteditor.login;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class LoginProvider extends ContentProvider {
    //Параметры БД
    private static final String DB_NAME = "users";
    private static final int DB_VERSION = 1;
    private static final String DB_ID = "_id";
    private static final String DB_COLUMN_EMAIL = "email";
    private static final String DB_COLUMN_PASSWORD = "password";
    private static final String DB_CREATE_SCRIPT = "create table " + DB_NAME + " ("
            + DB_ID + " Integer primary key autoincrement, " + DB_COLUMN_EMAIL + " text, "
            + DB_COLUMN_PASSWORD + " text" + ")";
    private static final String TAG = "MyLogs";

    //Параметры Uri
    static final String AUTHORITY = "MyDB";
    static final String PATH = "users";
    static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+PATH);

   // static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd" + AUTHORITY + "." + PATH;
   // static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd" + AUTHORITY + "." + PATH;

    static final int URI_USERS = 1;//Список пользователей
    static final int URI_USER_ID = 2;//Конкретный пользователь

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH, URI_USERS);
        uriMatcher.addURI(AUTHORITY, PATH, URI_USER_ID);
    }

    DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sql = dbHelper.getReadableDatabase();
        return sql.query(DB_NAME, null, selection, null, null, null, null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase sql = dbHelper.getWritableDatabase();
       long rowID =  sql.insert(DB_NAME, null, values);
        return ContentUris.withAppendedId(CONTENT_URI, rowID);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    //Создание БД
    private class DBHelper extends SQLiteOpenHelper {



    public DBHelper(Context context){
        super(context, "MyDB", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_SCRIPT);
        Log.d(TAG, "onCreateDBHelper");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    }
}
