package gxd.utils

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import org.greenrobot.eventbus.EventBus


class RequestStorage:AppCompatActivity(){
    val permissionArray = arrayOf<String>(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val permissionCode = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkPermission()){
            EventBus.getDefault().post(
                    gxd.test.TestPermissionActivity.MessageEvent(1, "权限已存在"))
            finish()
        }else {
            //请求权限
            ActivityCompat.requestPermissions(
                    this@RequestStorage,
                    permissionArray,
                    permissionCode)
            //显示权限请求信息
            EventBus.getDefault().post(
                    gxd.test.TestPermissionActivity.MessageEvent(-1, "发起请求中..."))
        }
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
        EventBus.getDefault().post(
                gxd.test.TestPermissionActivity.MessageEvent(
                        if (notOk) 0 else 100,
                        if (notOk) "请求失败" else "请求成功"))
        finish()
    }

    private fun checkPermission() = !permissionArray.any {
        PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this@RequestStorage, it)
    }
}