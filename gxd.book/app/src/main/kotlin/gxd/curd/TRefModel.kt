package gxd.curd

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import gxd.book.model.Todo
import io.realm.Realm
import io.realm.RealmObject
import org.jetbrains.anko.*
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * Created by Administrator on 2018/5/12.
 * 通用反射模型
 * 根据类名称（数据类），进行属性的反射并打包
 * 包装：对象相关的View及对象，列表项相关的序号分类等
 *
 * 输入：类名称及对象
 * 输出：属性名、值及其包装
 *
 * 作用：对象 <--> 字段视图
 * 对象 --> 字段视图
 * 字段视图 --> 对象
 */

class TRefModel private constructor(val className: String) {
    companion object {
        private val modelMap = hashMapOf<String, TRefModel>()
        private inline fun modelFromClassName(className: String): TRefModel {
            if (!modelMap.containsKey(className)) {
                modelMap.put(className, TRefModel(className))
            }
            return modelMap[className]!!
        }

        private inline fun getModel(obj: Any) = modelFromClassName(obj.javaClass.name)
        /**
         * 过滤字段（规则）
         */
        private inline fun getFields(model: TRefModel) = model.clazz.fields.filter { true }

        private fun createView(ctx: Context, obj: Any, isEdit: Boolean): View {
            val model = getModel(obj)
            val fields = getFields(model)
            //region test

            val getContent = model.clazz.declaredMethods[12]



            //endregion
            val lines = model.lines
            //等于0需要更新
            val isUpdate = lines.size == 0
            var rowInd = -1
            var colInd = -1

            return ctx.UI {
                verticalLayout {
                    lparams(width = ViewGroup.LayoutParams.MATCH_PARENT)
                    padding = ctx.dip(30)
                    fields.forEach {
                        val proName = it.name
                        val proValue = it.get(obj)
                        rowInd++
                        colInd = -1

                        linearLayout {
                            lparams(width = ViewGroup.LayoutParams.MATCH_PARENT)
                            setPadding(0, dip(8), 0, dip(8))
                            textView {
                                text = proName
                                colInd++
                            }.lparams(width = dip(260))
                            if (isEdit) {
                                textView {
                                    text = proValue.toString()
                                    colInd++
                                }
                            } else {
                                editText {
                                    setText(proValue.toString())
                                    colInd++
                                }
                            }
                        }
                        if (isUpdate) {
                            lines.add(Pair(rowInd, colInd))
                        }
                    }
                }
            }.view
        }

        /**
         * 从对象到视图
         */
        fun toView(ctx: Context, fromObj: Any) = createView(ctx, fromObj, false)

        /**
         * 从对象到可编辑视图
         */
        fun toEditView(ctx: Context, fromObj: Any) = createView(ctx, fromObj, true)

        /**
         * 从视图更新对象
         */
        fun updateObj(obj: Any, fromView: View) {
            val model = getModel(obj)
            val fields = getFields(model)
            val lines = model.lines

            var root = fromView as ViewGroup
            fun getProString(pair: Pair<Int, Int>): String {
                val line = root.getChildAt(pair.first) as ViewGroup
                val editText = line.getChildAt(pair.second) as EditText
                return editText.text.toString()
            }

            var rowInd = 0
            fields.forEach {
                it.set(obj, getProString(lines[rowInd]))
                rowInd++
            }
        }

        fun updateView(view:View, fromObj: Any){
            val model = getModel(fromObj)
            val fields = getFields(model)
            val lines = model.lines

            var root = view as ViewGroup
            fun setProString(pair: Pair<Int, Int>, strValue:String) {
                val line = root.getChildAt(pair.first) as ViewGroup
                val textView = line.getChildAt(pair.second) as TextView
                textView.text = strValue
            }

            var rowInd = 0
            fields.forEach {
                setProString(lines[rowInd], it.get(fromObj).toString())
                rowInd++
            }
        }
    }

    private val clazz: Class<*>
    private val lines = mutableListOf<Pair<Int, Int>>()

    init {
        clazz = Class.forName(className)
    }
}

