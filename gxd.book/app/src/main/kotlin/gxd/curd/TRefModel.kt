package gxd.curd

import android.content.Context
import android.graphics.Color
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
        val ENDKEY = "_ao"
        val regGetMethod by lazy { "get(.*)$ENDKEY".toRegex() }
        val regSetMethod by lazy { "set(.*)$ENDKEY".toRegex() }
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

        /**
         * 过滤方法及排序
         */
        private inline fun getMethods(model: TRefModel) = model.clazz
                .declaredMethods
                .filter { regSetMethod.matches(it.name) }
                .sortedBy { it.name.length }
        private inline fun buildLines(model: TRefModel){
            if (model.lines.size == 0){
                getMethods(model).forEach {
                    val methodName = it.name
                    val key = methodName.substring(3, methodName.length-3)
                    val propName = key[0].toLowerCase()+key.substring(1)
                    model.lines.add(MethodData(propName,"set$key$ENDKEY","get$key$ENDKEY"))
                }
            }
        }
        /**
         * 创建对象的视图（Panel）
         * 1 从方法中提前属性名
         * 2 生成视图和位置信息(一次)
         */
        private fun createView(ctx: Context, obj: Any, isEdit: Boolean): View {
            val model = getModel(obj)
            buildLines(model)
            val lines = model.lines

            var rowInd = -1
            var colInd = -1

            return ctx.UI {
                verticalLayout {
                    lparams(width = ViewGroup.LayoutParams.MATCH_PARENT)
                    padding = ctx.dip(30)
                    backgroundColor = Color.YELLOW
                    lines.forEach {
                        //val proValue = model.clazz.getMethod(it.getMName).invoke(obj)
                        //以后修改
                        val name = it.getMName
                        val getM = model.clazz.declaredMethods.first { it.name == name }
                        val proValue = getM.invoke(obj)
                        rowInd++
                        colInd = -1

                        linearLayout {
                            lparams(width = ViewGroup.LayoutParams.MATCH_PARENT)
                            setPadding(0, dip(8), 0, dip(8))
                            backgroundColor = Color.GREEN
                            textView {
                                text = it.propName
                                colInd++
                            }.lparams(width = dip(100))
                            if (isEdit) {
                                editText {
                                    setText(proValue.toString())
                                    colInd++
                                    it.position(rowInd,colInd)
                                }
                            } else {
                                textView {
                                    text = proValue.toString()
                                    colInd++
                                    it.position(rowInd,colInd)
                                }
                            }
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

            var root = fromView as ViewGroup
            model.lines.forEach {
                val line = root.getChildAt(it.rowInd) as ViewGroup
                val targetView = line.getChildAt(it.colInd) as TextView
                val proValueString = targetView.text.toString()
                val name = it.setMName
                val setM = model.clazz.declaredMethods.first { it.name == name }
                setM.invoke(obj, proValueString)
            }
        }

        fun updateView(view:View, fromObj: Any){
            val model = getModel(fromObj)

            var root = view as ViewGroup
            model.lines.forEach {
                val line = root.getChildAt(it.rowInd) as ViewGroup
                val name = it.getMName
                val getM = model.clazz.declaredMethods.first { it.name == name }
                val proValue = getM.invoke(fromObj)
                val targetView = line.getChildAt(it.colInd)
                when{
                    targetView is EditText -> targetView.setText("$proValue")
                    targetView is TextView -> targetView.text = "$proValue"
                }
            }
        }
    }

    private val clazz: Class<*>
    private val lines = mutableListOf<MethodData>()

    init {
        clazz = Class.forName(className)
    }

    /**
     * 解决位置和顺序的问题(属性名,set方法名,get方法名)
     */
    class MethodData(val propName:String, val setMName:String, val getMName:String){
        //对象panel中的位置
        var rowInd = 0
            private set
        var colInd = 0
            private set
        fun position(rowIndex:Int, colIndex:Int){
            rowInd = rowIndex
            colInd = colIndex
        }
    }
}

