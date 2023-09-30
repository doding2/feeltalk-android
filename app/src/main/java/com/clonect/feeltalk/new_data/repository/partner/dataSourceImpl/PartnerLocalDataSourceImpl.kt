package com.clonect.feeltalk.new_data.repository.partner.dataSourceImpl

import android.content.Context
import com.clonect.feeltalk.new_data.repository.partner.dataSource.PartnerLocalDataSource
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import java.io.File

/**
 * Created by doding2 on 2023/09/27.
 */
class PartnerLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper
): PartnerLocalDataSource {
    override suspend fun savePartnerInfo(partnerInfo: PartnerInfo) {
        val file = File(context.filesDir, "partnerInfo.dat")
        val encrypted = appLevelEncryptHelper.encryptObject("partnerInfo", partnerInfo)
        file.writeBytes(encrypted)
    }

    override suspend fun getPartnerInfo(): PartnerInfo? {
        val file = File(context.filesDir, "partnerInfo.dat")
        if (!file.exists() || !file.canRead()) return null
        val dataBytes = file.readBytes()
        val decrypted = appLevelEncryptHelper.decryptObject<PartnerInfo>("partnerInfo", dataBytes)
        return decrypted as? PartnerInfo
    }

}