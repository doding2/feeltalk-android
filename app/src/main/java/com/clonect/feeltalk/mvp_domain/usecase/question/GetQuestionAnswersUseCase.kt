package com.clonect.feeltalk.mvp_domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.mvp_domain.repository.QuestionRepository2
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class GetQuestionAnswersUseCase(
    private val userRepository: UserRepository,
    private val questionRepository2: QuestionRepository2,
) {
    suspend operator fun invoke(question: String): Resource<QuestionAnswersDto> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> questionRepository2.getQuestionAnswers(result.data, question)
            is Resource.Error -> Resource.Error(result.throwable)
        }
    }
}