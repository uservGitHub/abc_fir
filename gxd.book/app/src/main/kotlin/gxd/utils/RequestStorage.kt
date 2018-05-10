package gxd.utils

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.content.pm.PackageManager
import android.os.Bundle
import org.greenrobot.eventbus.EventBus


class RequestStorage:AppCompatActivity(){
    val permissionArray = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val permissionCode = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //请求权限
        /*ActivityCompat.requestPermissions(
                this@RequestStorage,
                permissionArray,
                permissionCode)*/
        //显示权限请求信息
        //...
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var notOk = true
        if (requestCode == permissionCode) {
            notOk = grantResults.any {
                PackageManager.PERMISSION_GRANTED != it
            }
        }
        //回调请求结果 !notOk
        //...
        //EventBus.getDefault().post(new MessageEvent(!notKo));
        EventBus.getDefault().post(gxd.test.TestPermissionActivity.MessageEvent(if (notOk) 0 else 100))
        finish()
    }

/*    private fun checkPermission() = !permissionArray.any {
        PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this@RequestStorage, it)
    }*/
}