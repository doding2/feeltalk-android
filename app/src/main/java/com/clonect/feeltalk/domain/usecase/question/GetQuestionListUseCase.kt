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
    suspend operator fun invoke(): Flow<Resource<List<Question>>> {
        val result = userRepository.getAccessToken()
        return if (result is Resource.Success) {
            questionRepository.getQuestionList(result.data)
        } else {
            flow { emit(Resource.Error(Exception("User is not logged in yet."))) }
        }
    }
}