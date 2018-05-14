package com.example.work.myapplication

import android.app.Application
import io.realm.Realm

/**
 * Created by work on 2018/5/14.
 */
class MyApplication:Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}