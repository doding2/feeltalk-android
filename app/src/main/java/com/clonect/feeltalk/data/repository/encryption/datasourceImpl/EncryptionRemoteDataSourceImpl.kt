package com.clonect.feeltalk.data.repository.encryption.datasourceImpl

import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPrivateKeyDto
import com.clonect.feeltalk.domain.model.data.encryption.LoadPartnerPublicKeyDto
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.model.dto.encryption.AppLevelAesKeyDto
import com.clonect.feeltalk.domain.model.dto.encryption.RestorePrivateKeysDto
import com.clonect.feeltalk.domain.model.dto.encryption.TempPublicKeyDto
import com.google.gson.JsonObject
import retrofit2.HttpException
import retrofit2.Response

class EncryptionRemoteDataSourceImpl(
    private val clonectService: ClonectService,
) : EncryptionRemoteDataSource {

    override suspend fun getAppLevelAesKey(accessToken: String): Response<AppLevelAesKeyDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.getAppLevelAesKey(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

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



    /** Restore Receiver **/
    override suspend fun requestKeyRestoring(accessToken: String): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.requestKeyRestoring(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun loadMyPublicKey(accessToken: String): Response<LoadPartnerPublicKeyDto> {
        val response = clonectService.loadMyPublicKey(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun uploadTempKey(
        accessToken: String,
        tempPublicKey: String,
    ): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("tempPublicKey", tempPublicKey)
        }
        val response = clonectService.uploadTempKey(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun restorePrivateKeys(accessToken: String): Response<RestorePrivateKeysDto> {
        val response = clonectService.restorePrivateKeys(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }


    /** Restore Sender **/
    override suspend fun acceptKeyRestoring(accessToken: String): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
        }
        val response = clonectService.acceptKeyRestoring(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun loadTempKey(accessToken: String): Response<TempPublicKeyDto> {
        val response = clonectService.loadTempKey(accessToken)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

    override suspend fun uploadTempEncryptedPrivateKey(
        accessToken: String,
        privateKey: String,
    ): Response<StatusDto> {
        val body = JsonObject().apply {
            addProperty("accessToken", accessToken)
            addProperty("tempPrivateKey", privateKey)
        }
        val response = clonectService.uploadTempEncryptedPrivateKey(body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body() == null) throw NullPointerException("Response body from server is null.")
        return response
    }

}