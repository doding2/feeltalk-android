package com.clonect.feeltalk.mvp_domain.model.dto.encryption

data class RestorePrivateKeysDto(
    val partnerPrivateKeyEncodeBySelfPublicKey: String?,
    val selfPrivateKeyEncodeByTempPublicKey: String?,
)
