package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question2
import com.clonect.feeltalk.domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.domain.model.dto.question.QuestionDetailDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.clonect.feeltalk.domain.model.dto.question.TodayQuestionAnswersDto
import kotlinx.coroutines.flow.Flow

interface QuestionRepository2 {

    suspend fun getTodayQuestion(accessToken: String): Resource<Question2>
    suspend fun getTodayQuestionAnswersFromServer(accessToken: String): Resource<TodayQuestionAnswersDto>
    suspend fun getQuestionDetail(accessToken: String, question: String): Resource<QuestionDetailDto>
    suspend fun sendQuestionAnswer(accessToken: String, question2: Question2): Resource<SendQuestionDto>

    suspend fun getQuestionList(accessToken: String): Flow<Resource<List<Question2>>>

    suspend fun getQuestionByContentFromDB(question: String): Resource<Question2>
    suspend fun getQuestionAnswers(accessToken: String, question: String): Resource<QuestionAnswersDto>

    suspend fun saveQuestionToDatabase(question2: Question2): Resource<Long>

}