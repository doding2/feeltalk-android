package com.clonect.feeltalk.data.api

import com.clonect.feeltalk.domain.model.user.GoogleTokenRequest
import com.clonect.feeltalk.domain.model.user.GoogleTokens
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GoogleAuthService {

    @POST("oauth2/v4/token")
    suspend fun fetchGoogleAuthInfo(
        @Body request: GoogleTokenRequest
    ): Response<GoogleTokens>

}