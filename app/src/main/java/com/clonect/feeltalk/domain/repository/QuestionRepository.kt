package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.clonect.feeltalk.domain.model.dto.question.TodayQuestionAnswersDto
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {

    suspend fun getTodayQuestion(accessToken: String): Resource<Question>
    suspend fun getTodayQuestionAnswersFromServer(accessToken: String): Resource<TodayQuestionAnswersDto>
    suspend fun sendQuestionAnswer(accessToken: String, question: Question): Resource<SendQuestionDto>

    suspend fun getQuestionList(accessToken: String): Flow<Resource<List<Question>>>

    suspend fun getQuestionByContentFromDB(question: String): Resource<Question>
    suspend fun getQuestionAnswers(accessToken: String, question: String): Resource<QuestionAnswersDto>

    suspend fun saveQuestionToDatabase(question: Question): Resource<Long>

}