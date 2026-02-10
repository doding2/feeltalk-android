package com.clonect.feeltalk.mvp_data.repository.question.datasourceImpl

import com.clonect.feeltalk.mvp_data.db.QuestionDao
import com.clonect.feeltalk.mvp_data.repository.question.datasource.QuestionLocalDataSource2
import com.clonect.feeltalk.mvp_domain.model.data.question.Question2
import kotlinx.coroutines.flow.Flow

class QuestionLocalDataSource2Impl(
    private val dao: QuestionDao
): QuestionLocalDataSource2 {

    override suspend fun getQuestionListFlow(): Flow<List<Question2>> {
        return dao.getQuestionListFlow()
    }


    override suspend fun getTodayQuestion(date: String): Question2? {
        val questionList = dao.getQuestionListByDate(date)
        if (questionList.isEmpty())
            return null
        return questionList.last()
    }

    override suspend fun saveOneQuestion(question2: Question2): Long {
        return dao.insertQuestion(question2)
    }

    override suspend fun saveQuestionList(question2List: List<Question2>): List<Long> {
        return dao.insertQuestionList(question2List)
    }

    override suspend fun getQuestionByContent(content: String): Question2? {
        val questionList = dao.getQuestionListByContent(content)
        if (questionList.isEmpty())
            return null
        return questionList.last()
    }


}