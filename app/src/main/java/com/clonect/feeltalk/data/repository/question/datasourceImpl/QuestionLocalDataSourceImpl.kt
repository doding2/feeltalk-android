package com.clonect.feeltalk.data.repository.question.datasourceImpl

import com.clonect.feeltalk.data.db.QuestionDao
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource
import com.clonect.feeltalk.domain.model.data.question.Question

class QuestionLocalDataSourceImpl(
    private val dao: QuestionDao
): QuestionLocalDataSource {

    override suspend fun getTodayQuestion(date: String): Question? {
        val questionList = dao.getQuestionListByDate(date)
        if (questionList.isEmpty())
            return null
        return questionList.last()
    }

    override suspend fun saveOneQuestion(question: Question): Long {
        return dao.insertQuestion(question)
    }

    override suspend fun getQuestionByContent(content: String): Question? {
        val questionList = dao.getQuestionListByContent(content)
        if (questionList.isEmpty())
            return null
        return questionList.last()
    }


}