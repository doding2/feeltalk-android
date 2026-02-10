package com.clonect.feeltalk.release_domain.usecase.newAccount

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_domain.model.account.RequestAdultAuthCodeDto
import com.clonect.feeltalk.release_domain.repository.account.AccountRepository

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