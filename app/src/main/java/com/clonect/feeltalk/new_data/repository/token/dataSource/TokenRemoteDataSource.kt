package com.clonect.feeltalk.new_data.repository.token.dataSource

import com.clonect.feeltalk.new_domain.model.newAccount.LogInNewResponse
import com.clonect.feeltalk.new_domain.model.token.RenewTokenDto
import com.clonect.feeltalk.new_domain.model.token.TokenInfo

interface TokenRemoteDataSource {

    suspend fun updateFcmToken(accessToken: String, fcmToken: String)

    suspend fun renewToken(tokenInfo: TokenInfo): RenewTokenDto

    suspend fun reissueToken(tokenInfo: TokenInfo): LogInNewResponse

}