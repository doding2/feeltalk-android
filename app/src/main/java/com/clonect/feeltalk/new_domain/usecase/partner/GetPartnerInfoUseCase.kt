package com.clonect.feeltalk.new_domain.usecase.partner

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_domain.repository.partner.PartnerRepository
import com.clonect.feeltalk.new_domain.repository.token.TokenRepository

class GetPartnerInfoUseCase(
    private val tokenRepository: TokenRepository,
    private val partnerRepository: PartnerRepository,
) {
    suspend operator fun invoke(): Resource<PartnerInfo> {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            return Resource.Error(tokenInfo.throwable)
        }
        val accessToken = (tokenInfo as Resource.Success).data.accessToken
        return partnerRepository.getPartnerInfo(accessToken)
    }
}