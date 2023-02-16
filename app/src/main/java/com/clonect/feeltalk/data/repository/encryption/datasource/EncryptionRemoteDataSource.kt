package com.clonect.feeltalk.data.repository.encryption.datasource

import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.encryption.AppLevelAesKeyDto
import retrofit2.Response

interface EncryptionRemoteDataSource {
    suspend fun getAppLevelAesKey(accessToken: String): Response<AppLevelAesKeyDto>

    suspend fun uploadMyPublicKey(accessToken: String, publicKey: String): Response<StatusDto>
    suspend fun loadPartnerPublicKey(accessToken: String): Response<LoadPartnerPublicKeyDto>

    suspend fun uploadMyPrivateKey(accessToken: String, encryptedPrivateKey: String): Response<StatusDto>
    suspend fun loadPartnerPrivateKey(accessToken: String): Response<LoadPartnerPrivateKeyDto>


    suspend fun sendMyPrivateKeyRecoveryRequest(accessToken: String): Response<String>
}