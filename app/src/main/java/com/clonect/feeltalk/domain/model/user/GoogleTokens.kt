package com.clonect.feeltalk.domain.model.user

/**
 *   LogInGoogleResponse
 *
 *  "access_token": "string",
 *  "expires_in": 0,
 *  "scope": "string",
 *  "token_type": "string"
 *  "id_token": "string"
 */

data class GoogleTokens(
    var access_token: String = "",
    var refresh_token: String = "",
    var expires_in: Int = 0,
    var scope: String = "",
    var token_type: String = "",
    var id_token: String = "",
)