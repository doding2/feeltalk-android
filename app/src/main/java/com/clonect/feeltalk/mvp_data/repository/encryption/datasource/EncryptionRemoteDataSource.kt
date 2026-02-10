package com.clonect.feeltalk.mvp_data.repository.encryption.datasource

import com.clonect.feeltalk.mvp_domain.model.data.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.mvp_domain.model.data.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.mvp_domain.model.dto.common.StatusDto
import com.clonect.feeltalk.mvp_domain.model.dto.encryption.AppLevelAesKeyDto
import com.clonect.feeltalk.mvp_domain.model.dto.encryption.RestorePrivateKeysDto
import com.clonect.feeltalk.mvp_domain.model.dto.encryption.TempPublicKeyDto
import retrofit2.Response

interface EncryptionRemoteDataSource {
    suspend fun getAppLevelAesKey(accessToken: String): Response<AppLevelAesKeyDto>

    suspend fun uploadMyPublicKey(accessToken: String, publicKey: String): Response<StatusDto>
    suspend fun loadPartnerPublicKey(accessToken: String): Response<LoadPartnerPublicKeyDto>

    suspend fun uploadMyPrivateKey(accessToken: String, encryptedPrivateKey: String): Response<StatusDto>
    suspend fun loadPartnerPrivateKey(accessToken: String): Response<LoadPartnerPrivateKeyDto>


    /** Restore Receiver **/
    suspend fun requestKeyRestoring(accessToken: String): Response<StatusDto>
    suspend fun loadMyPublicKey(accessToken: String): Response<LoadPartnerPublicKeyDto>
    suspend fun uploadTempKey(accessToken: String, tempPublicKey: String): Response<StatusDto>
    suspend fun restorePrivateKeys(accessToken: String): Response<RestorePrivateKeysDto>


    /** Restore Sender **/
    suspend fun acceptKeyRestoring(accessToken: String): Response<StatusDto>
    suspend fun loadTempKey(accessToken: String): Response<TempPublicKeyDto>
    suspend fun uploadTempEncryptedPrivateKey(accessToken: String, privateKey: String): Response<StatusDto>
}