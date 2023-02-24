package com.clonect.feeltalk.domain.usecase.encryption

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.repository.EncryptionRepository
import com.clonect.feeltalk.domain.repository.UserRepository

class RestoreKeysUseCase(
    private val userRepository: UserRepository,
    private val encryptionRepository: EncryptionRepository
) {
    suspend operator fun invoke(): Resource<StatusDto> {
        val accessToken = userRepository.getAccessToken()
        return when (accessToken) {
            is Resource.Success -> encryptionRepository.restoreKeys(accessToken.data)
            is Resource.Error -> Resource.Error(accessToken.throwable)
            is Resource.Loading -> Resource.Loading(accessToken.isLoading)
        }
    }
}