package gxd.test

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import gxd.android.startBundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        verticalLayout {
            tbMessage = textView {
                text = ""
                textSize = 44F
                textColor = Color.GREEN
            }
            button {
                text = "调用权限请求"
                setOnClickListener {
                    tbMessage.text = "调用请求页面中..."
                    RequestStorage.check(this@TestPermissionActivity)
                }
            }
        }
        RequestStorage.check(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        tbMessage.text = "接收：${event}"
        when(event.code){
            RequestStorage.SUCCESS -> longToast(event.title!!)
            RequestStorage.FAILURE -> longToast(event.title!!)
            RequestStorage.ALLREADY -> longToast(event.title!!)
        }
    }
}