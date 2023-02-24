package com.clonect.feeltalk.domain.model.dto.encryption

data class RestorePrivateKeysDto(
    val partnerPrivateKeyEncodeBySelfPublicKey: String?,
    val selfPrivateKeyEncodeByTempPublicKey: String?,
)
