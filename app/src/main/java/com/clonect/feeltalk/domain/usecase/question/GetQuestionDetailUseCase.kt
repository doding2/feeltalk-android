package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.question.QuestionDetailDto
import com.clonect.feeltalk.domain.repository.QuestionRepository2
import com.clonect.feeltalk.domain.repository.UserRepository

class GetQuestionDetailUseCase(
    private val userRepository: UserRepository,
    private val questionRepository2: QuestionRepository2
) {
    suspend operator fun invoke(question: String): Resource<QuestionDetailDto> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> questionRepository2.getQuestionDetail(result.data, question)
            is Resource.Error -> Resource.Error(result.throwable)
        }
    }
}