package com.clonect.feeltalk.release_domain.model.challenge

data class ChallengeCountDto(
    var totalCount: Long,
    var ongoingCount: Long,
    var completedCount: Long
): java.io.Serializable
