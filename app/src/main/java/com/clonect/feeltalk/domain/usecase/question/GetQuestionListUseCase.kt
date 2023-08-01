package com.clonect.feeltalk.domain.usecase.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question2
import com.clonect.feeltalk.domain.repository.QuestionRepository2
import com.clonect.feeltalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetQuestionListUseCase(
    private val userRepository: UserRepository,
    private val questionRepository2: QuestionRepository2,
) {
    suspend operator fun invoke(): Flow<Resource<List<Question2>>> {
        val result = userRepository.getAccessToken()
        return if (result is Resource.Success) {
            questionRepository2.getQuestionList(result.data)
        } else {
            flow { emit(Resource.Error(Exception("User is not logged in yet."))) }
        }
    }
}