package com.clonect.feeltalk.new_domain.repository.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.signIn.CheckMemberTypeDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface SignInRepository {

    suspend fun checkMemberType(socialToken: SocialToken): Resource<CheckMemberTypeDto>
    suspend fun signUp(socialToken: SocialToken, nickname: String): Resource<TokenInfo>

}