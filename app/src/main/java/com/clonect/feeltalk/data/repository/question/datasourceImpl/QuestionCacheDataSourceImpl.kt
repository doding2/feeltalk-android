package com.clonect.feeltalk.data.repository.question.datasourceImpl

import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource
import com.clonect.feeltalk.domain.model.data.question.Question

class QuestionCacheDataSourceImpl: QuestionCacheDataSource {

    private var todayQuestion: Question? = null
    private val questionMap: MutableMap<String, Question> = mutableMapOf()

    override fun getTodayQuestion(): Question? = todayQuestion

    override fun saveTodayQuestion(todayQuestion: Question) {
        this.todayQuestion = todayQuestion
        questionMap[todayQuestion.question] = todayQuestion
    }


    override fun getQuestionList(): List<Question> {
        return questionMap.values.toList()
    }

    override fun saveQuestionList(questionList: List<Question>) {
        questionList.forEach {
            questionMap[it.question] = it
        }
    }

    override fun saveOneQuestion(question: Question) {
        questionMap[question.question] = question
    }

}