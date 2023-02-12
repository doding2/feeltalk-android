package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.domain.repository.QuestionRepository
import com.clonect.feeltalk.domain.repository.UserRepository

class GetTodayQuestionAnswersFromServer(
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(): Resource<QuestionAnswersDto> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> questionRepository.getTodayQuestionAnswersFromServer(result.data)
            is Resource.Error -> Resource.Error(result.throwable)
            is Resource.Loading -> Resource.Loading(result.isLoading)
        }
    }
}