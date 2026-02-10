package com.clonect.feeltalk.release_domain.model.account

import java.io.Serializable

enum class SocialType(val raw: String): Serializable {
    Kakao("kakao"),
    Naver("naver"),
    Google("google"),
    Apple("apple")
}