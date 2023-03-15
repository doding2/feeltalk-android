package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.dto.question.QuestionDetailDto
import com.clonect.feeltalk.domain.repository.QuestionRepository
import com.clonect.feeltalk.domain.repository.UserRepository

class GetQuestionDetailUseCase(
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(question: String): Resource<QuestionDetailDto> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> questionRepository.getQuestionDetail(result.data, question)
            is Resource.Error -> Resource.Error(result.throwable)
            is Resource.Loading -> Resource.Loading(result.isLoading)
        }
    }
}