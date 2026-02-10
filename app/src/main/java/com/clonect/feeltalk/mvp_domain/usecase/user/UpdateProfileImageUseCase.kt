package com.clonect.feeltalk.mvp_domain.usecase.user

import android.graphics.Bitmap
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.user.ProfileImageUrlDto
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class UpdateProfileImageUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(image: Bitmap): Resource<ProfileImageUrlDto> {
        return userRepository.updateMyProfileImage(image)
    }
}