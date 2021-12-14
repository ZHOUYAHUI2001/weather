package com.example.myapplication.base;

import android.app.Application;
import com.example.myapplication.db.DBManager;
import org.xutils.x;

public class UniteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);//this表示全局声明
        DBManager.initDB(this);//初始化数据库（当项目工程被创建同时初始化数据库）
    }
}
