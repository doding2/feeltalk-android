package com.clonect.feeltalk.release_data.repository.token.dataSource

import com.clonect.feeltalk.release_domain.model.token.SocialToken
import com.clonect.feeltalk.release_domain.model.token.TokenInfo

interface TokenCacheDataSource {

    fun saveSocialToken(socialToken: SocialToken)
    fun getSocialToken(): SocialToken?

    fun saveTokenInfo(tokenInfo: TokenInfo)
    fun getTokenInfo(): TokenInfo?

}