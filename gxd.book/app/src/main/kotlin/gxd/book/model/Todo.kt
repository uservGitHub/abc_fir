package gxd.book.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by jack on 2017/7/21.
 */
@RealmClass
open class Todo : RealmObject() {
    @PrimaryKey
    open var id_ao: String = "-1"
    open var title_ao: String = "日程"
    open var content_ao: String = "事项"
    //open var aaa:Int = 100
}
