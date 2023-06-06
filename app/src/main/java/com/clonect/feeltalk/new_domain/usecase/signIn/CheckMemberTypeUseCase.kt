package com.clonect.feeltalk.new_domain.usecase.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.signIn.CheckMemberTypeDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.repository.signIn.SignInRepository

class CheckMemberTypeUseCase(
    private val signInRepository: SignInRepository
) {
    suspend operator fun invoke(socialToken: SocialToken): Resource<CheckMemberTypeDto> {
        return signInRepository.checkMemberType(socialToken)
    }
}