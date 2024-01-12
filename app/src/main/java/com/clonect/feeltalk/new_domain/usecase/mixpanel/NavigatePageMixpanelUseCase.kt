package com.clonect.feeltalk.new_domain.usecase.mixpanel

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.repository.account.AccountRepository
import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class NavigatePageMixpanelUseCase(
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke() {
        mixpanelRepository.navigatePage()
    }
}