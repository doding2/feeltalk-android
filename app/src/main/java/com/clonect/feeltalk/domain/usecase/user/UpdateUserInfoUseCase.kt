package com.clonect.feeltalk.domain.usecase.user

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.common.StatusDto
import com.clonect.feeltalk.domain.repository.UserRepository
import java.text.SimpleDateFormat
import java.util.*

class UpdateUserInfoUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(nickname: String, birthDate: String, anniversary: String): Resource<StatusDto> {
        val format = SimpleDateFormat("yyyy", Locale.getDefault())
        val currentYear = format.format(Date())
        val birthYear = birthDate.substringBefore("/")
        val age = currentYear.toLong() - birthYear.toLong() + 1

        return userRepository.updateUserInfo(nickname, age, birthDate, anniversary)
    }

}