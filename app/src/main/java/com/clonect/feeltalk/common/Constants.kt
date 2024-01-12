package com.clonect.feeltalk.common

class Constants {
    companion object {
        const val CLONECT_BASE_URL = "https://clonect.net/"

        const val APPLE_AUTH_BASE_URL = "https://appleid.apple.com/auth/authorize"
        const val APPLE_AUTH_CLIENT_ID = "clonect.com.feeltalk"
        const val APPLE_AUTH_REDIRECT_URI = "https://clonect.net/login/apple"

        const val APP_LEVEL_KEY_PROVIDER = "AndroidKeyStore"
        const val APP_LEVEL_KEY_ALIAS = "AppLevelKey"
        const val APP_LEVEL_CIPHER_ALGORITHM = "AES/GCM/NoPadding"

        const val MIXPANEL_DEBUG_TOKEN = "9f50c4144f59f59ed17bc8dfc922ae75"
        const val MIXPANEL_RELEASE_TOKEN = "e7341c990703953973ddb05e871b4929"


        const val VOICE_CACHE_FILE_NAME = "voiceCache.wav"
        const val IMAGE_CACHE_FILE_NAME = "imageCache.png"
        const val CHAT_PAGE_SIZE = 4
        const val QUESTION_PAGE_SIZE = 4
        const val ONGOING_CHALLENGE_PAGE_SIZE = 4




        const val EXCHANGE_KEY_WAIT_DELAY = 500L

        const val ONE_DAY = 24 * 60 * 60 * 1000

        const val PILLOWTALK_FEEDBACK = "pillowtalk.feedback@gmail.com"
    }
}