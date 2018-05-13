package gxd.test

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import gxd.android.sdPath
import gxd.android.startBundle
import gxd.book.MainActivity
import gxd.utils.FileRootActivity
import gxd.utils.MessageEvent
import gxd.utils.RequestStorage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*


/**
 * Created by work on 2018/5/10.
 */

class TestPermissionActivity:AppCompatActivity(){
    private lateinit var tbMessage:TextView
    private lateinit var tbFileRoot:TextView
    private var targetClassTag = ""

    private val pushCT:(String)->Unit = {
        targetClassTag = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        verticalLayout {
            button {
                text = "调用Todo"
                setOnClickListener {
                    startBundle(MainActivity::class.java)
                }
            }
            tbMessage = textView {
                text = ""
                textSize = 40F
                textColor = Color.GREEN
            }
            button {
                text = "调用权限请求"
                setOnClickListener {
                    tbMessage.text = "调用请求页面中..."
                    pushCT(RequestStorage::class.java.name)
                    RequestStorage.check(this@TestPermissionActivity)
                }
            }
            tbFileRoot = textView {
                textSize = 40F
                textColor = Color.BLUE
            }
            button {
                text = "选择文件根路径"
                setOnClickListener {
                    pushCT(FileRootActivity::class.java.name)
                    FileRootActivity.select(
                            this@TestPermissionActivity,
                            applicationContext.sdPath.absolutePath)
                }
            }
        }
        //RequestStorage.check(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when(targetClassTag){
            RequestStorage::class.java.name ->{
                tbMessage.text = "接收：${event}"
                when(event.code){
                    RequestStorage.SUCCESS -> longToast(event.title!!)
                    RequestStorage.FAILURE -> longToast(event.title!!)
                    RequestStorage.ALLREADY -> longToast(event.title!!)
                }
            }
            FileRootActivity::class.java.name ->{
                when(event.code){
                    FileRootActivity.SELECTED -> tbFileRoot.text = event.data!!.toString()
                    //其他情况没改变，无需更新
                }
            }
        }
    }
}