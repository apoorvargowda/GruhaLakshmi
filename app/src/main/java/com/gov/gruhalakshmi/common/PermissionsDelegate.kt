package com.gov.gruhalakshmi.common

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal class PermissionsDelegate(private val activity: Activity) {

    val PERMS_SUCCESS = 1
    val PERMS_SETTINGS = 2
    val PERMS_DENIED = 3

    private val FACE_PERMS = arrayOf(
        android.Manifest.permission.CAMERA
    )

    fun hasCameraPermission(): Boolean {
        return FACE_PERMS.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }

    }

    fun requestCameraPermission() {
        ActivityCompat.requestPermissions(activity, FACE_PERMS, REQUEST_CODE)
    }


    fun resultGranted(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Int {

        var allperms:Boolean = false
        if (requestCode == REQUEST_CODE) {
            var incr: Int = 0

            permissions.forEach {
                if (grantResults[incr] == PackageManager.PERMISSION_DENIED) {
                    val showRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, it);
                    if (!showRationale) {
                        allperms = false
                        return PERMS_SETTINGS
                    }else{
                        allperms = false
                        return PERMS_DENIED
                    }

                }else{
                    allperms = true
                }
                incr++
            }
        }
        if(allperms) {
            return PERMS_SUCCESS
        }else{
            return PERMS_DENIED
        }

    }

    companion object {
        private const val REQUEST_CODE = 10
    }

}