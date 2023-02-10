package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.question.Question

interface QuestionRepository {

    suspend fun getTodayQuestion(accessToken: String): Resource<Question>
    suspend fun sendQuestionAnswer(accessToken: String, question: Question): Resource<String>

}