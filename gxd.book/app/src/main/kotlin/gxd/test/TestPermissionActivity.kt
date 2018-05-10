package gxd.test

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import gxd.android.startBundle
import gxd.utils.RequestStorage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.button
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView


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
                    startBundle(RequestStorage::class.java){
                        putString("permission", "storage")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        tbMessage.text = "接收：${event}"
    }

    data class MessageEvent(val code:Int, val message:String="")
}