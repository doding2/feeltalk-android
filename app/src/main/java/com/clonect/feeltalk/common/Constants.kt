package com.clonect.feeltalk.common

class Constants {
    companion object {
        const val CLONECT_BASE_URL = "https://clonect.net/"

        const val GOOGLE_AUTH_CLIENT_ID = "422145577673-9pi0ubc9n5k1soetj6bn9uh4l0d76hm9.apps.googleusercontent.com"
        const val GOOGLE_AUTH_CLIENT_SECRET = "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"

        const val APPLE_AUTH_BASE_URL = "https://appleid.apple.com/auth/authorize"
        const val APPLE_AUTH_CLIENT_ID = "clonect.com.feeltalk"
        const val APPLE_AUTH_REDIRECT_URI = "https://clonect.net/login/apple"



        const val APP_LEVEL_KEY_PROVIDER = "AndroidKeyStore"
        const val APP_LEVEL_KEY_ALIAS = "AppLevelKey"
        const val APP_LEVEL_CIPHER_ALGORITHM = "AES/GCM/NoPadding"


        const val PAGE_SIZE = 4


        const val EXCHANGE_KEY_WAIT_DELAY = 500L

        const val ONE_DAY = 24 * 60 * 60 * 1000
    }
}