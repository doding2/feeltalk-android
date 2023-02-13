package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto

interface QuestionRepository {

    suspend fun getTodayQuestion(accessToken: String): Resource<Question>
    suspend fun getTodayQuestionAnswersFromServer(accessToken: String): Resource<QuestionAnswersDto>

    suspend fun sendQuestionAnswer(accessToken: String, question: Question): Resource<SendQuestionDto>

    suspend fun getQuestionList(accessToken: String): Resource<List<Question>>

    suspend fun getQuestionByContentFromDB(question: String): Resource<Question>

}