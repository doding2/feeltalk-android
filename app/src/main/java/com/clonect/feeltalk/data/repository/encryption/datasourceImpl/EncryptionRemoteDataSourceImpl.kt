package com.clonect.feeltalk.data.repository.encryption.datasourceImpl

import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.repository.encryption.datasource.EncryptionRemoteDataSource
import retrofit2.Response
import java.security.PrivateKey
import java.security.PublicKey

class EncryptionRemoteDataSourceImpl(
    private val clonectService: ClonectService,
) : EncryptionRemoteDataSource {

    override suspend fun uploadMyPublicKey(publicKey: PublicKey): Response<String> {
        throw Exception("Not yet implemented.")
    }

    override suspend fun loadPartnerPublicKey(): Response<String> {
        throw Exception("Not yet implemented.")
    }

    override suspend fun sendMyPrivateKeyToPartner(privateKey: PrivateKey): Response<String> {
        throw Exception("Not yet implemented.")
    }

    override suspend fun sendMyPrivateKeyRecoveryRequest(): Response<String> {
        throw Exception("Not yet implemented.")
    }

}