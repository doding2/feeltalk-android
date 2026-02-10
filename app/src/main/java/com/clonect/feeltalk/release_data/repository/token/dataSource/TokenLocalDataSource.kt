package com.clonect.feeltalk.release_data.repository.token.dataSource

import com.clonect.feeltalk.release_domain.model.token.TokenInfo

interface TokenLocalDataSource {

    fun saveTokenInfo(tokenInfo: TokenInfo)
    fun getTokenInfo(): TokenInfo?

    fun deleteAll(): Boolean
}