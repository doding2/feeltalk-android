package com.clonect.feeltalk.release_domain.usecase.mixpanel

import com.clonect.feeltalk.release_domain.repository.mixpanel.MixpanelRepository

class SetInQuestionPageMixpanelUseCase(
    private val mixpanelRepository: MixpanelRepository,
) {
    suspend operator fun invoke(isInQuestion: Boolean) {
        mixpanelRepository.setInQuestionPage(isInQuestion)
    }
}