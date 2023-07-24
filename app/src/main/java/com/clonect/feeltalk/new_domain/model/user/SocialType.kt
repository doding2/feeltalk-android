package com.clonect.feeltalk.new_domain.model.user

import java.io.Serializable

enum class SocialType(val raw: String): Serializable {
    Kakao("kakao"),
    Naver("naver"),
    Google("google"),
    Apple("appleAndroid")
}