package com.clonect.feeltalk.data.repository.encryption.datasource

import com.clonect.feeltalk.domain.model.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.domain.model.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.domain.model.encryption.StatusResponse
import com.clonect.feeltalk.domain.model.user.AccessToken
import retrofit2.Response
import java.security.PublicKey

interface EncryptionRemoteDataSource {
    suspend fun uploadMyPublicKey(accessToken: AccessToken, publicKey: PublicKey): Response<StatusResponse>
    suspend fun loadPartnerPublicKey(accessToken: AccessToken): Response<LoadPartnerPublicKeyDto>

    suspend fun uploadMyPrivateKey(accessToken: AccessToken, encryptedPrivateKey: String): Response<StatusResponse>
    suspend fun loadPartnerPrivateKey(accessToken: AccessToken): Response<LoadPartnerPrivateKeyDto>


    suspend fun sendMyPrivateKeyRecoveryRequest(accessToken: AccessToken): Response<String>
}