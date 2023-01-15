package com.clonect.feeltalk.data.api

import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ClonectService {

    @POST("api/member")
    suspend fun signUpWithEmail(
        @Body signUpEmailRequest: SignUpEmailRequest
    ): Response<SignUpEmailResponse>?

}