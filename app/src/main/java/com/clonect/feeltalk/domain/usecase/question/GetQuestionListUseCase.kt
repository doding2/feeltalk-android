package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.repository.QuestionRepository
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetQuestionListUseCase(
    private val userRepository: UserRepository,
    private val questionRepository: QuestionRepository,
) {
    operator fun invoke(): Flow<Resource<List<Question>>> = flow {
        val accessToken = userRepository.getAccessToken()
        val list = when (accessToken) {
            is Resource.Success -> questionRepository.getQuestionList(accessToken.data)
            is Resource.Error -> Resource.Error(accessToken.throwable)
            is Resource.Loading -> Resource.Loading(accessToken.isLoading)
        }
        emit(list)
    }
}