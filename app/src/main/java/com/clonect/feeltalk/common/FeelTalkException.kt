package com.clonect.feeltalk.common


sealed class FeelTalkException(override val message: String): Exception(message) {
    class NoMyKeyException(override val message: String): FeelTalkException(message)
    class NoPartnerKeyException(override val message: String): FeelTalkException(message)
}