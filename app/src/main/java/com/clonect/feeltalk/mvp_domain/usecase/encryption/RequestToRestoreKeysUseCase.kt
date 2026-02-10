package com.clonect.feeltalk.mvp_domain.usecase.encryption

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.common.StatusDto
import com.clonect.feeltalk.mvp_domain.repository.EncryptionRepository
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class RequestToRestoreKeysUseCase(
    private val userRepository: UserRepository,
    private val encryptionRepository: EncryptionRepository
) {
    suspend operator fun invoke(): Resource<StatusDto> {
        val accessToken = userRepository.getAccessToken()
        return when (accessToken) {
            is Resource.Success -> encryptionRepository.requestToRestoreKeys(accessToken.data)
            is Resource.Error -> Resource.Error(accessToken.throwable)
        }
    }
}