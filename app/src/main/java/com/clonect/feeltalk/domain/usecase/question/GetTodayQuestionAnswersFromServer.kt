package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.question.TodayQuestionAnswersDto
import com.clonect.feeltalk.domain.repository.QuestionRepository
import com.clonect.feeltalk.domain.repository.UserRepository

class GetTodayQuestionAnswersFromServer(
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
) {
    suspend operator fun invoke(): Resource<TodayQuestionAnswersDto> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> questionRepository.getTodayQuestionAnswersFromServer(result.data)
            is Resource.Error -> Resource.Error(result.throwable)
        }
    }
}