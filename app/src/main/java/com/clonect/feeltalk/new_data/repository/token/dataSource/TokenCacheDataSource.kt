package com.clonect.feeltalk.new_data.repository.token.dataSource

import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface TokenCacheDataSource {

    fun saveSocialToken(socialToken: SocialToken)
    fun getSocialToken(): SocialToken?

    fun saveTokenInfo(tokenInfo: TokenInfo)
    fun getTokenInfo(): TokenInfo?

}