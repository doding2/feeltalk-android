package com.clonect.feeltalk.mvp_domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.common.StatusDto
import com.clonect.feeltalk.mvp_domain.model.dto.encryption.RestorePrivateKeysDto

interface EncryptionRepository {
    suspend fun getAppLevelAesKey(accessToken: String): Resource<String>
    suspend fun checkKeyPairsExist(): Resource<Boolean>
    suspend fun checkKeyPairsWorkWell(): Resource<Boolean>

    suspend fun uploadMyPublicKey(accessToken: String): Resource<String>
    suspend fun loadPartnerPublicKey(accessToken: String): Resource<String>
    suspend fun uploadMyPrivateKey(accessToken: String): Resource<String>
    suspend fun loadPartnerPrivateKey(accessToken: String): Resource<String>

    suspend fun encryptMyText(message: String): Resource<String>
    suspend fun decryptMyText(digest: String): Resource<String>
    suspend fun encryptPartnerText(message: String): Resource<String>
    suspend fun decryptPartnerText(digest: String): Resource<String>

    suspend fun requestToRestoreKeys(accessToken: String): Resource<StatusDto>
    suspend fun restoreKeys(accessToken: String): Resource<RestorePrivateKeysDto>
    suspend fun helpToRestoreKeys(accessToken: String): Resource<StatusDto>
}