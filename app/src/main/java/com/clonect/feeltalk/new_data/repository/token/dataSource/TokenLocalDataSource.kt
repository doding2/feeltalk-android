package com.clonect.feeltalk.new_data.repository.token.dataSource

import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface TokenLocalDataSource {

    fun saveTokenInfo(tokenInfo: TokenInfo)
    fun getTokenInfo(): TokenInfo?

    fun deleteAll(): Boolean
}