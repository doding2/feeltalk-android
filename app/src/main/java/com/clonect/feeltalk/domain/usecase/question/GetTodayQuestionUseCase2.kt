package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question2
import com.clonect.feeltalk.domain.repository.QuestionRepository2
import com.clonect.feeltalk.domain.repository.UserRepository

class GetTodayQuestionUseCase2(
    private val questionRepository2: QuestionRepository2,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Resource<Question2> {
        val accessToken = userRepository.getAccessToken()
        return when (accessToken) {
            is Resource.Success -> questionRepository2.getTodayQuestion(accessToken.data)
            is Resource.Error -> Resource.Error(accessToken.throwable)
        }
    }

}