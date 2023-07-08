package com.clonect.feeltalk.new_domain.repository.signIn

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface TokenRepository {
    fun cacheSocialToken(socialToken: SocialToken): Resource<Unit>
    fun getCachedSocialToken(): Resource<SocialToken>

    fun saveTokenInfo(tokenInfo: TokenInfo): Resource<Unit>
    suspend fun getTokenInfo(): Resource<TokenInfo>
}