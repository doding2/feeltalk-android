package com.clonect.feeltalk.new_domain.repository.question

import androidx.paging.PagingData
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_domain.model.chat.ShareQuestionChatDto
import com.clonect.feeltalk.new_domain.model.question.LastQuestionPageNoDto
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.question.QuestionListDto
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun getLastChatPageNo(accessToken: String): Resource<LastQuestionPageNoDto>
    suspend fun getQuestionList(accessToken: String, pageNo: Long): Resource<QuestionListDto>
    fun getPagingQuestion(): Flow<PagingData<Question>>

    suspend fun getQuestion(accessToken: String, index: Long): Resource<Question>
    suspend fun getTodayQuestion(accessToken: String): Resource<Question>
    fun changeTodayQuestionCache(question: Question)
    suspend fun getTodayQuestionFlow(): Flow<Question?>

    suspend fun answerQuestion(accessToken: String, question: Question, myAnswer: String): Resource<Unit>
    suspend fun pressForAnswer(accessToken: String, index: Long): Resource<Unit>

    suspend fun shareQuestion(accessToken: String, index: Long): Resource<ShareQuestionChatDto>

    suspend fun answerPartnerQuestionCache(question: Question)
    suspend fun getAnswerQuestionFlow(): Flow<Question>
}