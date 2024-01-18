package com.clonect.feeltalk.new_domain.usecase.mixpanel

import com.clonect.feeltalk.new_domain.repository.mixpanel.MixpanelRepository

class SetInContentShareMixpanelUseCase(
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(isInContentShare: Boolean) {
        mixpanelRepository.setInContentShare(isInContentShare)
    }
}