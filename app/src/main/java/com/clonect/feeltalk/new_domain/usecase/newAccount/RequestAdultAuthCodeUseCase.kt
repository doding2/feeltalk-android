package com.clonect.feeltalk.new_domain.usecase.newAccount

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.RequestAdultAuthCodeDto
import com.clonect.feeltalk.new_domain.model.account.SocialType
import com.clonect.feeltalk.new_domain.model.newAccount.GetUserStatusNewResponse
import com.clonect.feeltalk.new_domain.model.newAccount.LogInNewResponse
import com.clonect.feeltalk.new_domain.model.newAccount.SignUpNewResponse
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken

class RequestAdultAuthCodeUseCase(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        providerId: String,
        userName: String,
        userPhone: String,
        userBirthday: String,
        userGender: String,
        userNation: String,
    ): Resource<RequestAdultAuthCodeDto> {
        return accountRepository.requestAdultAuthCode(providerId, userName, userPhone, userBirthday, userGender, userNation)
    }
}