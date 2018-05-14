package com.example.work.myapplication.model

/**
 * Created by work on 2018/5/14.
 */

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects

open class Cat : RealmObject() {
    var name: String? = null

    @LinkingObjects("cats")
    val owners: RealmResults<Person>? = null
}
