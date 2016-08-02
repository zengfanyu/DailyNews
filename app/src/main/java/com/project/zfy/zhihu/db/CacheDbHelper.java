package com.project.zfy.zhihu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库的帮助类
 *
 * @author zfy
 * @created at 2016/8/2 13:03
 */
public class CacheDbHelper extends SQLiteOpenHelper {

    public CacheDbHelper(Context context, int version) {
        super(context, "cache.db", null, version);
    }


    @Override  //创建表
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists CacheList (" +
                "id INTEGER primary key autoincrement," +
                "date INTEGER unique," +
                "json text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
