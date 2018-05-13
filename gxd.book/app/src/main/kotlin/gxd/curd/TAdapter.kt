package gxd.curd

/**
 * Created by Administrator on 2018/5/12.
 */

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import gxd.book.R
import gxd.book.model.Todo
import io.realm.RealmBasedRecyclerViewAdapter
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.RealmViewHolder
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip

class TAdapter<T: RealmObject>(val context: Context,
                                  realmResults: RealmResults<T>,
                                  automaticUpdate: Boolean,
                                  animateResults: Boolean,
                                  private val clickListener: TodoItemClickListener<T>?=null) :

        RealmBasedRecyclerViewAdapter<T, TAdapter<T>.ViewHolder>(
                context,
                realmResults,
                automaticUpdate,
                animateResults) {

    override fun onCreateRealmViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        /*val v = inflater.inflate(R.layout.todo_item, viewGroup, false)
        return ViewHolder(v, clickListener)*/
        /*val className = realmResults[0].javaClass.name
        val v = TRefModel.fromClassName(className).fetchPanel(context)
        return ViewHolder(v, clickListener)*/
        val obj = realmResults[0]
        return ViewHolder(TRefModel.toView(context, realmResults.first() as Todo), clickListener)
    }

    override fun onBindRealmViewHolder(viewHolder: ViewHolder, position: Int) {
        val current = realmResults[position]
        TRefModel.updateView(viewHolder.view, current)

        /*val todo = realmResults[position]

        viewHolder.todoTitle.setText(todo.title)
        viewHolder.todoTitle.fontFeatureSettings = "font-size:40px"
        viewHolder.todoTitle.setTextColor(Color.argb(255, 69, 106, 124))

        viewHolder.todoContent.setText(todo.content)*/

        /*val todo = realmResults[position] as Todo
        viewHolder.tv1.text = todo.title
        viewHolder.tv2.text = todo.content*/
    }

    interface TodoItemClickListener<T> {
        fun onClick(caller: View, todo: T)
    }

    inner class ViewHolder(val view: View, private val clickListener: TodoItemClickListener<T>?) :
            RealmViewHolder(view), View.OnClickListener {
        init {
            // Bind annotated fields and methods
            ButterKnife.bind(this, view)
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            //clickListener?.onClick(v, realmResults[adapterPosition])
        }
    }
}


