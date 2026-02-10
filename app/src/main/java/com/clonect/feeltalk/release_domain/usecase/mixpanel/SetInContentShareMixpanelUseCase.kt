package com.clonect.feeltalk.release_domain.usecase.mixpanel

import com.clonect.feeltalk.release_domain.repository.mixpanel.MixpanelRepository

class SetInContentShareMixpanelUseCase(
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(isInContentShare: Boolean) {
        mixpanelRepository.setInContentShare(isInContentShare)
    }
}