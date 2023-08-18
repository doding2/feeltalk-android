package com.clonect.feeltalk.common

import retrofit2.HttpException
import retrofit2.Response


sealed class FeelTalkException(override val message: String): Exception(message) {
    class NoMyKeyException(override val message: String = "내 공개키, 비밀키가 존재하지 않습니다."): FeelTalkException(message)
    class NoPartnerKeyException(override val message: String = "연인의 공개키, 비밀키가 존재하지 않습니다."): FeelTalkException(message)

    class EncryptionFailureException(override val message: String = "텍스트 암호화에 실패했습니다."): FeelTalkException(message)
    class DecryptionFailureException(override val message: String = "텍스트 복호화에 실패했습니다."): FeelTalkException(message)

    class ServerIsDownException(response: Response<*>): HttpException(response)
}