package com.clonect.feeltalk.release_domain.model.appSettings

import java.io.Serializable

sealed class Language(val englishName: String, val nativeName: String): Serializable {
    object Korean: Language("korean", "한국어")
    object English: Language("english", "English")
    object Japanese: Language("japanese", "日本語")
    object Chinese: Language("chinese", "中國語")
}
