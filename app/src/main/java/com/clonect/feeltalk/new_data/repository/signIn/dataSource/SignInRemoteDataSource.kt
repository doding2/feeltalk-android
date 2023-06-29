package com.clonect.feeltalk.new_data.repository.signIn.dataSource

import com.clonect.feeltalk.new_domain.model.signIn.CheckMemberTypeDto
import com.clonect.feeltalk.new_domain.model.signIn.CoupleCodeDto
import com.clonect.feeltalk.new_domain.model.signIn.SignUpDto
import com.clonect.feeltalk.new_domain.model.token.SocialToken

interface SignInRemoteDataSource {

    suspend fun checkMemberType(socialToken: SocialToken): CheckMemberTypeDto
    suspend fun signUp(socialToken: SocialToken, nickname: String): SignUpDto

    suspend fun getCoupleCode(accessToken: String): CoupleCodeDto
    suspend fun matchCouple(accessToken: String, coupleCode: String)


}