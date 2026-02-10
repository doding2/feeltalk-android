package com.clonect.feeltalk.release_domain.model.account

import com.clonect.feeltalk.release_domain.model.appSettings.Language
import java.io.Serializable

data class ConfigurationInfo(
    val isLock: Boolean,
    val language: Language
): Serializable