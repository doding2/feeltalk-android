package com.clonect.feeltalk.mvp_domain.usecase.encryption

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.repository.EncryptionRepository
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class LoadPartnerPublicKeyUseCase(
    private val userRepository: UserRepository,
    private val encryptionRepository: EncryptionRepository,
) {

    suspend operator fun invoke(): Resource<String> {
        val accessToken = userRepository.getAccessToken()
        return when (accessToken) {
            is Resource.Success -> encryptionRepository.loadPartnerPublicKey(accessToken.data)
            is Resource.Error -> Resource.Error(accessToken.throwable)
        }
    }

}