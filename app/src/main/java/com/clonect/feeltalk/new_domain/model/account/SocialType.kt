package com.clonect.feeltalk.new_domain.model.account

import java.io.Serializable

enum class SocialType(val raw: String): Serializable {
    Kakao("kakao"),
    Naver("naver"),
    Google("google"),
    AppleAndroid("appleAndroid"),
    AppleIOS("appleIOS")
}