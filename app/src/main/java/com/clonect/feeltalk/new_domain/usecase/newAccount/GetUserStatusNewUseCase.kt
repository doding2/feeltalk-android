package com.clonect.feeltalk.new_domain.usecase.newAccount

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.SocialType
import com.clonect.feeltalk.new_domain.model.newAccount.GetUserStatusNewResponse
import com.clonect.feeltalk.new_domain.model.newAccount.LogInNewResponse
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class GetUserStatusNewUseCase(
    private val tokenRepository: TokenRepository,
    private val accountRepository: AccountRepository,
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(): Resource<GetUserStatusNewResponse> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        val result = accountRepository.getUserStatusNew(accessToken)

        if (result is Resource.Success && result.data.memberStatus.lowercase() != "newbie") {
            val myInfo = accountRepository.getMyInfo(accessToken)
            if (myInfo is Resource.Success) {
                mixpanelRepository.logIn(myInfo.data.id)
            }
        }

        return result
    }
}