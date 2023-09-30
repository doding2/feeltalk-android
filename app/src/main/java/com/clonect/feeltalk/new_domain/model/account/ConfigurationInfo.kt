package com.clonect.feeltalk.new_domain.model.account

import com.clonect.feeltalk.new_domain.model.appSettings.Language
import java.io.Serializable

data class ConfigurationInfo(
    val isLock: Boolean,
    val language: Language
): Serializable