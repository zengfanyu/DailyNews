package com.project.zfy.zhihu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.zfy.zhihu.global.Constant;

/**
 * $desc
 * Created by zfy on 2016/8/5.
 */
public class WebCacheDbHelper extends SQLiteOpenHelper {
    public WebCacheDbHelper(Context context, int version) {
        super(context, Constant.NEWS_CONTENT_DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists Cache (" +
                "id INTEGER primary key autoincrement," +
                "newsId INTEGER unique," +
                "json text)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
