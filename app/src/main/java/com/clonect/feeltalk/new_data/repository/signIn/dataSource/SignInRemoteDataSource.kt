package com.clonect.feeltalk.new_data.repository.signIn.dataSource

import com.clonect.feeltalk.new_domain.model.signIn.CheckMemberTypeDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface SignInRemoteDataSource {

    suspend fun checkMemberType(socialToken: SocialToken): CheckMemberTypeDto
    suspend fun signUp(socialToken: SocialToken, nickname: String): TokenInfo


}