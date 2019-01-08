package com.liyi.testcustom

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_PERMISSIONS_REQUEST = 999
    }
    private var permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )
    private val mPermissionList: ArrayList<String> = ArrayList()

    private var mAudioRecord:AudioRecord? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (getPermission()) {
            getNoise()
        } else {
            Toast.makeText(this,"请同意所有权限,否则不可使用" , Toast.LENGTH_SHORT).show()
            val permissions = mPermissionList.toArray(arrayOfNulls<String>(mPermissionList.size))//将List转为数组
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST)
        }
//        var percent = 0.5f
//        var temp = 0.01f
//        Thread {
//            run {
//                while (true) {
//                    if (percent <= 0f || percent >= 1f) {
//                        temp = -temp
//                    }
//                    percent += temp
//                    bvTest.setPercent(percent)
//                    Thread.sleep(10)
//                }
//            }
//        }.start()
    }

    private fun getNoise() {
        mAudioRecord = AudioRecord().apply {
            setListener { percent ->
                bvTest.setPercent(percent.toFloat())
            }
            getNoise()
        }
    }

    private fun getPermission(): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            for (per in permissions) {
                val havPer = PermissionChecker.checkSelfPermission(this, per) == PermissionChecker.PERMISSION_GRANTED
                if (!havPer) {
                    return false
                }
            }
            return true
        } else {
            mPermissionList.clear()
            for (i in 0 until permissions.size) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i])
                }
            }
            if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
                return true
            }
            return false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            getNoise()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        mAudioRecord?.stop()
        mAudioRecord = null
        super.onDestroy()
    }
}
