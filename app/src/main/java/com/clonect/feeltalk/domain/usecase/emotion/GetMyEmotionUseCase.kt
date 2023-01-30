package com.clonect.feeltalk.domain.usecase.emotion

import android.util.Log
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.news.News
import com.clonect.feeltalk.domain.model.news.NewsType
import com.clonect.feeltalk.domain.model.user.Emotion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextUInt

class GetMyEmotionUseCase {
    operator fun invoke(): Flow<Resource<Emotion>> = flow {
        val testEmotion = when (Random.nextInt(0..3) ) {
            0 -> Emotion.Happy
            1 -> Emotion.Puzzling
            2 -> Emotion.Bad
            3 -> Emotion.Angry
            else -> Emotion.Happy
        }

        emit(Resource.Success(testEmotion))
    }
}