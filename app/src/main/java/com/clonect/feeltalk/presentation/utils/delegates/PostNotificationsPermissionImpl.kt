package com.clonect.feeltalk.presentation.utils.delegates

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.clonect.feeltalk.presentation.utils.showAlertDialog

class PostNotificationsPermissionImpl: PostNotificationsPermission {

    override var onPostNotificationGranted: (Boolean) -> Unit = {}

    override fun Fragment.checkPostNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permission = Manifest.permission.POST_NOTIFICATIONS

        val isAlreadyGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

        when {
            isAlreadyGranted -> {
                onPostNotificationGranted(true)
            }
            else -> {
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    onPostNotificationGranted(isGranted)
                }.also {
                    it.launch(permission)
                }
            }
        }
    }

}