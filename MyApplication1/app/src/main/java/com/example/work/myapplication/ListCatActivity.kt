package com.example.work.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.ListView
import com.example.work.myapplication.model.Cat
import com.example.work.myapplication.model.Person
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
    lateinit var frame:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()

        verticalLayout {
            button {
                text = "保存"
                setOnClickListener {
                    realm.executeTransaction {
                        val person = Person(100)
                        WhatView.updateObj(person, frame.getChildAt(1))
                        realm.copyToRealm(person)
                    }

                }

            }
            button{
                text = "读取"
                setOnClickListener {
                    val cats = realm.where<Person>().findAll()!!
                    val data = realm.copyFromRealm(cats)
                    frame.removeAllViews()
                    data.forEach {
                        frame.addView(WhatView.toEditView(ctx, it))
                    }
                }
            }
            frame = linearLayout {
                orientation = LinearLayout.VERTICAL
            }.lparams(matchParent, wrapContent)
        }

        //whatAdapter.setData(realm.copyFromRealm(cats))
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}