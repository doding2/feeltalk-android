package com.clonect.feeltalk.data.repository.encryption.datasource

import retrofit2.Response
import java.security.PrivateKey
import java.security.PublicKey

interface EncryptionRemoteDataSource {
    suspend fun uploadMyPublicKey(publicKey: PublicKey): Response<String>
    suspend fun loadPartnerPublicKey(): Response<String>

    // TODO 암호화 시켜서 서버에 전달, response로 상대의 비밀키를 양도받음
    suspend fun sendMyPrivateKeyToPartner(privateKey: PrivateKey): Response<String>

    // TODO 나중에 내 비밀키 복구용
    suspend fun sendMyPrivateKeyRecoveryRequest(): Response<String>
}