package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.repository.QuestionRepository
import com.clonect.feeltalk.domain.repository.UserRepository

class SendQuestionAnswerUseCase(
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(question: Question): Resource<String> {
        val accessToken = userRepository.getAccessToken()
        return when (accessToken) {
            is Resource.Success -> questionRepository.sendQuestionAnswer(accessToken.data, question)
            is Resource.Error -> Resource.Error(accessToken.throwable)
            is Resource.Loading -> Resource.Loading(accessToken.isLoading)
        }
    }
}