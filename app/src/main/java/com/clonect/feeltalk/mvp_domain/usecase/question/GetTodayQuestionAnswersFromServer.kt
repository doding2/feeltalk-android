package com.clonect.feeltalk.mvp_domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.question.TodayQuestionAnswersDto
import com.clonect.feeltalk.mvp_domain.repository.QuestionRepository2
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class GetTodayQuestionAnswersFromServer(
    private val userRepository: UserRepository,
    private val questionRepository2: QuestionRepository2,
) {
    suspend operator fun invoke(): Resource<TodayQuestionAnswersDto> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> questionRepository2.getTodayQuestionAnswersFromServer(result.data)
            is Resource.Error -> Resource.Error(result.throwable)
        }
    }
}