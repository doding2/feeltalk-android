package com.clonect.feeltalk.data.repository.user

import android.util.Log
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.data.api.ClonectService
import com.clonect.feeltalk.data.api.GoogleAuthService
import com.clonect.feeltalk.domain.model.user.LogInGoogleRequest
import com.clonect.feeltalk.domain.model.user.LogInGoogleResponse
import com.clonect.feeltalk.domain.model.user.SignUpEmailRequest
import com.clonect.feeltalk.domain.model.user.SignUpEmailResponse
import com.clonect.feeltalk.data.util.Result
import com.clonect.feeltalk.domain.repository.UserRepository
import java.util.concurrent.CancellationException

class UserRepositoryImpl(
    private val clonectService: ClonectService,
    private val googleAuthService: GoogleAuthService
): UserRepository {

    override suspend fun signUpWithEmail(
        signUpEmailRequest: SignUpEmailRequest
    ): Result<SignUpEmailResponse> {
        try {
            clonectService.signUpWithEmail(
                signUpEmailRequest
            )?.run {
                return Result.Success(this.body() ?: SignUpEmailResponse())
            } ?: return Result.Error(Exception("Fail to sign up with email Exception"))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.i("UserRepository", "Fail to Sign Up With Email: $e")
        }
        return Result.Error(Exception("Fail to sign up with email Exception"))
    }

    override suspend fun fetchGoogleAuthInfo(
        authCode: String
    ): Result<LogInGoogleResponse> {
        try {
            googleAuthService.fetchGoogleAuthInfo(
                LogInGoogleRequest(
                    grant_type = "authorization_code",
                    client_id = BuildConfig.GOOGLE_AUTH_CLIENT_ID,
                    client_secret = BuildConfig.GOOGLE_AUTH_CLIENT_SECRET,
                    redirect_uri = "",
                    code = authCode
                )
            )?.run {
                return Result.Success(this.body() ?: LogInGoogleResponse())
            } ?: return Result.Error(Exception("Fail to get Google AccessToken Exception"))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.i("UserRepository", "Fail to fetch Google Auth Info: $e")
        }
        return Result.Error(Exception("Fail to get Google AccessToken Exception"))
    }

}