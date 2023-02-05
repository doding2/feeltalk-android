package com.clonect.feeltalk.data.repository.encryption.datasourceImpl

import android.util.Base64
import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.domain.model.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.domain.model.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.domain.model.user.AccessToken
import com.google.gson.JsonObject
import retrofit2.Response
import java.security.PrivateKey
import java.security.PublicKey

class EncryptionRemoteDataSourceImpl(
    private val clonectService: ClonectService,
) : EncryptionRemoteDataSource {

    override suspend fun uploadMyPublicKey(accessToken: AccessToken, publicKey: PublicKey): Response<String> {
        val publicBytes = Base64.encode(publicKey.encoded, Base64.DEFAULT)
        val publicString = String(publicBytes)
        val obj = JsonObject().apply {
            addProperty("publicKey", publicString)
            addProperty("accessToken", accessToken.value)
        }
        return clonectService.uploadMyPublicKey(obj)
    }

    override suspend fun loadPartnerPublicKey(accessToken: AccessToken): Response<LoadPartnerPublicKeyDto> {
        return clonectService.loadPartnerPublicKey(accessToken.value)
    }

    override suspend fun uploadMyPrivateKey(accessToken: AccessToken, encryptedPrivateKey: String): Response<String> {
        val obj = JsonObject().apply {
            addProperty("accessToken", accessToken.value)
            addProperty("privateKey", encryptedPrivateKey)
        }
        return clonectService.uploadMyPrivateKey(obj)
    }

    override suspend fun loadPartnerPrivateKey(accessToken: AccessToken): Response<LoadPartnerPrivateKeyDto> {
        return clonectService.loadPartnerPrivateKey(accessToken.value)
    }




    override suspend fun sendMyPrivateKeyRecoveryRequest(accessToken: AccessToken): Response<String> {
        throw Exception("Not yet implemented.")
    }

}