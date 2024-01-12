package com.clonect.feeltalk.new_data.repository.mixpanel.dataSourceImpl

import android.content.Context
import com.clonect.feeltalk.new_data.repository.mixpanel.dataSource.MixpanelLocalDataSource
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import okhttp3.internal.format
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by doding2 on 2024/01/12.
 */
class MixpanelLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
) : MixpanelLocalDataSource {

    override suspend fun saveUserActiveDate(date: String) {
        val file = File(context.filesDir, "userActiveDate.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("userActiveDate", date)
        file.writeBytes(encrypted)
    }

    override suspend fun getUserActiveDate(): String? {
        val file = File(context.filesDir, "userActiveDate.dat")
        if (!file.exists() || !file.canRead()) return null
        val dataBytes = file.readBytes()
        val decrypted = appLevelEncryptHelper.decryptObject<String>("userActiveDate", dataBytes)
        return decrypted as? String
    }

    override suspend fun savePageNavigationCount(date: String, count: Long) {
        val pair = date to count
        val file = File(context.filesDir, "pageNavigationCount.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("pageNavigationCount", pair)
        file.writeBytes(encrypted)
    }

    override suspend fun getPageNavigationCount(): Pair<String, Long>? {
        val file = File(context.filesDir, "pageNavigationCount.dat")
        if (!file.exists() || !file.canRead()) return null
        val dataBytes = file.readBytes()
        val decrypted = appLevelEncryptHelper.decryptObject<String>("pageNavigationCount", dataBytes)
        return decrypted as? Pair<String, Long>
    }

}