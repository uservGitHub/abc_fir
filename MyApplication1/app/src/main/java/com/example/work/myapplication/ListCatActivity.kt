package com.example.work.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.ListView
import com.example.work.myapplication.model.Cat
import com.example.work.myapplication.waitreplace.WhatAdapter
import com.example.work.myapplication.waitreplace.WhatView
import io.realm.Realm
import io.realm.kotlin.where
import org.jetbrains.anko.*

/**
 * Created by work on 2018/5/15.
 */

class ListCatActivity:AppCompatActivity(){
    lateinit var realm:Realm
    //lateinit var listView:ListView
    //lateinit var whatAdapter:WhatAdapter<Cat>
    lateinit var frame:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()


        verticalLayout {
            frame = linearLayout {
                orientation = LinearLayout.VERTICAL
                textView {
                    text = "Hello"
                }
            }.lparams(matchParent, wrapContent)
        }

        //whatAdapter.setData(realm.copyFromRealm(cats))
    }

    override fun onResume() {
        super.onResume()
        val cats = realm.where<Cat>().findAll()!!
        val cat = realm.copyFromRealm(cats.first()!!)
       try {
           val view = WhatView.toView(ctx,cat)
           frame.addView(view)
       }catch (e:Exception){
           e.printStackTrace()
       }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}