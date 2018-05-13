package gxd.book

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import gxd.book.R
import gxd.book.model.Todo
import gxd.curd.TRefModel
import io.realm.Realm
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.find
import java.util.*


class TodoEditFragment : Fragment() {
    val realm: Realm = Realm.getDefaultInstance()
    var todo: Todo? = null

    companion object {
        val TODO_ID_KEY: String = "todo_id_key"

        fun newInstance(id: String): TodoEditFragment {
            var args: Bundle = Bundle()
            args.putString(TODO_ID_KEY, id)
            var todoEditFragment: TodoEditFragment = newInstance()
            todoEditFragment.arguments = args
            return todoEditFragment
        }

        fun newInstance(): TodoEditFragment {
            return TodoEditFragment()
        }
    }
    lateinit var hostView:View
    lateinit var btnCommand:Button
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hostView = TRefModel.toEditView(context,Todo())
        return UI {
            // AnkoContext
            val frame = verticalLayout {
                padding = dip(30)
                addView(hostView)

                btnCommand = button {
                    // button 视图
                    id = R.id.todo_add
                    textResource = R.string.add_todo
                    textColor = Color.WHITE
                    setBackgroundColor(Color.DKGRAY)
                    //onClick { _ -> createTodoFrom(title, content) }
                    //onClick { createTodoFrom(title, content) }
                    setOnClickListener {
                        saveTodo()
                    }
                }
            }
        }.view

        //return TRefModel.toView(context, Todo())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments.containsKey(TODO_ID_KEY)) {

            val todoId = arguments.getString(TODO_ID_KEY)
            //todo = realm.where(Todo::class.java).equalTo("id", todoId).findFirst()
            //val todos = realm!!.where(Todo::class.java).findAll()
            todo = realm.where(Todo::class.java).equalTo("id_ao", todoId).findFirst()
            TRefModel.updateView(hostView, todo!!)

            /*
            val todoTitle = find<EditText>(R.id.todo_title)
            todoTitle.setText(todo?.title_ao)

            val todoContent = find<EditText>(R.id.todo_content)
            todoContent.setText(todo?.content_ao)*/

            /*val add = find<Button>(R.id.todo_add)
            add.setText(R.string.save)*/
            btnCommand.setText(R.string.save)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
    private fun saveTodo() {

        realm.beginTransaction()
        // Either update the edited object or create a new one.
        /*var t = todo ?: realm.createObject(Todo::class.java)
        t.id_ao = todo?.id_ao ?: UUID.randomUUID().toString()
        t.title_ao = title.text.toString()
        t.content_ao = todoContent.text.toString()*/
        var t = todo ?: realm.createObject(Todo::class.java)
        try{
            TRefModel.updateObj(t, hostView)
        }catch (e:Exception){
            e.printStackTrace()
        }
        realm.commitTransaction()

        activity.supportFragmentManager.popBackStack()
        // 返回 MainActivity
//        val intent = Intent()
//        intent.setClass(activity, MainActivity::class.java)
//        activity.startActivity(intent)
    }

    /**
     *  新增待办事项，存入Realm数据库
     *
     *  @param title the title edit text.
     *  @param todoContent the content edit text.
     */
    private fun createTodoFrom(title: EditText, todoContent: EditText) {

        realm.beginTransaction()
        // Either update the edited object or create a new one.
        var t = todo ?: realm.createObject(Todo::class.java)
        t.id_ao = todo?.id_ao ?: UUID.randomUUID().toString()
        t.title_ao = title.text.toString()
        t.content_ao = todoContent.text.toString()

        realm.commitTransaction()

        activity.supportFragmentManager.popBackStack()
        // 返回 MainActivity
//        val intent = Intent()
//        intent.setClass(activity, MainActivity::class.java)
//        activity.startActivity(intent)
    }


}
