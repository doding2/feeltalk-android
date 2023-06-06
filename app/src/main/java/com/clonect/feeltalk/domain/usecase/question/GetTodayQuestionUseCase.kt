package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.repository.QuestionRepository
import com.clonect.feeltalk.domain.repository.UserRepository

class GetTodayQuestionUseCase(
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Resource<Question> {
        val accessToken = userRepository.getAccessToken()
        return when (accessToken) {
            is Resource.Success -> questionRepository.getTodayQuestion(accessToken.data)
            is Resource.Error -> Resource.Error(accessToken.throwable)
        }
    }

}