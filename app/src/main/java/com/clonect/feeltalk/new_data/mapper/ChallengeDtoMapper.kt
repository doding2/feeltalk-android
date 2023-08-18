package com.clonect.feeltalk.new_data.mapper

import com.clonect.feeltalk.new_domain.model.challenge.Challenge
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeCategory
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeDto
import com.clonect.feeltalk.new_domain.model.challenge.ChallengeListDto
import java.text.SimpleDateFormat
import java.util.*

fun ChallengeListDto.toChallengeList(): List<Challenge> {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val newList = mutableListOf<Challenge>()
    for (challengeDto in challengeList) {
        newList.add(
            challengeDto.toChallenge(format)
        )
    }
    return newList
}

fun ChallengeDto.toChallenge(format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())): Challenge {
    return run {
        Challenge(
            index = index,
            category = ChallengeCategory.fromString(category),
            title = title,
            body = content ?: "",
            deadline = format.parse(deadline) ?: throw NullPointerException("Fail to parse challenge deadline"),
            owner = creator,
            isCompleted = isCompleted
        )
    }
}