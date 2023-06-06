package com.clonect.feeltalk.new_data.repository.token.dataSourceImpl

import com.clonect.feeltalk.new_data.repository.token.dataSource.TokenCacheDataSource
import com.clonect.feeltalk.new_domain.model.token.SocialToken
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

class TokenCacheDataSourceImpl: TokenCacheDataSource {

    private var socialToken: SocialToken? = null
    private var tokenInfo: TokenInfo? = null

    override fun saveSocialToken(socialToken: SocialToken) {
        this.socialToken = socialToken
    }
    override fun getSocialToken() = socialToken

    override fun saveTokenInfo(tokenInfo: TokenInfo) {
        this.tokenInfo = tokenInfo
    }
    override fun getTokenInfo() = tokenInfo
}