package com.clonect.feeltalk.new_data.repository.token.dataSource

import com.clonect.feeltalk.new_domain.model.token.RenewTokenDto
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface TokenRemoteDataSource {

    suspend fun renewToken(tokenInfo: TokenInfo): RenewTokenDto

}