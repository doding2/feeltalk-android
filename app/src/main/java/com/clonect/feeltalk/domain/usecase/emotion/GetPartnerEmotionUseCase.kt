package com.clonect.feeltalk.domain.usecase.emotion

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.news.News
import com.clonect.feeltalk.domain.model.data.news.NewsType
import com.clonect.feeltalk.domain.model.data.user.Emotion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.random.nextInt

class GetPartnerEmotionUseCase {
    operator fun invoke(): Flow<Resource<Emotion>> = flow {
        val testEmotion = when (Random.nextInt(0..3)) {
            0 -> Emotion.Angry
            1 -> Emotion.Bad
            2 -> Emotion.Puzzling
            3 -> Emotion.Happy
            else -> Emotion.Happy
        }

        emit(Resource.Success(testEmotion))
    }
}