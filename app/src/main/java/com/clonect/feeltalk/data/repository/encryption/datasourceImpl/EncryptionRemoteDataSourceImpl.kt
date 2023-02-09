package com.clonect.feeltalk.data.repository.encryption.datasourceImpl

import android.util.Base64
import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.domain.model.dto.user.AccessTokenDto
import com.google.gson.JsonObject
import retrofit2.HttpException
import retrofit2.Response
import java.security.PublicKey

class EncryptionRemoteDataSourceImpl(
    private val clonectService: ClonectService,
) : EncryptionRemoteDataSource {

    override suspend fun uploadMyPublicKey(accessToken: String, publicKey: String): Response<StatusDto> {
        val obj = JsonObject().apply {
            addProperty("publicKey", publicKey)
            addProperty("accessToken", accessToken)
        }

        val response = clonectService.uploadMyPublicKey(obj)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun loadPartnerPublicKey(accessToken: String): Response<LoadPartnerPublicKeyDto> {
        val response = clonectService.loadPartnerPublicKey(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun uploadMyPrivateKey(accessToken: String, encryptedPrivateKey: String): Response<StatusDto> {
        val obj = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("privateKey", encryptedPrivateKey)
        }
        val response = clonectService.uploadMyPrivateKey(obj)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun loadPartnerPrivateKey(accessToken: String): Response<LoadPartnerPrivateKeyDto> {
        val response = clonectService.loadPartnerPrivateKey(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }



    override suspend fun sendMyPrivateKeyRecoveryRequest(accessToken: String): Response<String> {
        throw Exception("Not yet implemented.")
    }

}