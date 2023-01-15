package com.clonect.feeltalk.data.api

import com.clonect.feeltalk.domain.model.user.LogInGoogleRequest
import com.clonect.feeltalk.domain.model.user.LogInGoogleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GoogleAuthService {

    @POST("oauth2/v4/token")
    suspend fun fetchGoogleAuthInfo(
        @Body request: LogInGoogleRequest
    ): Response<LogInGoogleResponse>?

}