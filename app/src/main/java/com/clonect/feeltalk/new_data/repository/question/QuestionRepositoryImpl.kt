package com.clonect.feeltalk.new_data.repository.question

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.clonect.feeltalk.common.Constants
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.new_data.mapper.toQuestion
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionCacheDataSource
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionLocalDataSource
import com.clonect.feeltalk.new_data.repository.question.dataSource.QuestionRemoteDataSource
import com.clonect.feeltalk.new_data.repository.question.paging.QuestionPagingSource
import com.clonect.feeltalk.new_domain.model.chat.ShareQuestionChatDto
import com.clonect.feeltalk.new_domain.model.question.LastQuestionPageNoDto
import com.clonect.feeltalk.new_domain.model.question.PressForAnswerChatResponse
import com.clonect.feeltalk.new_domain.model.question.Question
import com.clonect.feeltalk.new_domain.model.question.QuestionListDto
import com.clonect.feeltalk.new_domain.repository.question.QuestionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class QuestionRepositoryImpl(
    private val cacheDataSource: QuestionCacheDataSource,
    private val localDataSource: QuestionLocalDataSource,
    private val remoteDataSource: QuestionRemoteDataSource,
    private val pagingSource: QuestionPagingSource,
): QuestionRepository {
    override suspend fun getLastChatPageNo(accessToken: String): Resource<LastQuestionPageNoDto> {
        return try {
            val result = remoteDataSource.getLastQuestionPageNo(accessToken)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getQuestionList(accessToken: String, pageNo: Long): Resource<QuestionListDto> {
        return try {
            val result = remoteDataSource.getQuestionList(accessToken, pageNo)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getPagingQuestion(): Flow<PagingData<Question>> {
        return Pager(
            PagingConfig(
                pageSize = Constants.QUESTION_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            pagingSource
        }.flow
    }

    override suspend fun getQuestion(accessToken: String, index: Long): Resource<Question> {
        return try {
            val result = remoteDataSource.getQuestion(accessToken, index)
            Resource.Success(result.toQuestion())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getTodayQuestion(accessToken: String): Resource<Question> {
        return try {
            val cache = cacheDataSource.getTodayQuestion()
            if (cache != null) {
                return Resource.Success(cache)
            }

            val result = remoteDataSource
                .getTodayQuestion(accessToken)
                .toQuestion()
            cacheDataSource.saveTodayQuestion(result)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun changeTodayQuestionCache(question: Question?) {
        cacheDataSource.saveTodayQuestion(question)
        localDataSource.setQuestionUpdated(true)
        cacheDataSource.setQuestionUpdated(true)
    }

    override suspend fun getTodayQuestionFlow(): Flow<Question?> {
        return cacheDataSource.getTodayQuestionFlow()
    }

    override suspend fun answerQuestion(
        accessToken: String,
        question: Question,
        myAnswer: String,
    ): Resource<Unit> {
        return try {
            val result = remoteDataSource.answerQuestion(accessToken, question.index, myAnswer)
            cacheDataSource.saveAnswerQuestion(question.copy(myAnswer = myAnswer))
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun pressForAnswer(accessToken: String, index: Long): Resource<PressForAnswerChatResponse> {
        return try {
            val result = remoteDataSource.pressForAnswer(accessToken, index)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun shareQuestion(accessToken: String, index: Long): Resource<ShareQuestionChatDto> {
        return try {
            val result = remoteDataSource.shareQuestion(accessToken, index)
            Resource.Success(result)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun answerPartnerQuestionCache(question: Question) {
        cacheDataSource.saveAnswerQuestion(question)
        localDataSource.setQuestionUpdated(true)
        cacheDataSource.setQuestionUpdated(true)
    }

    override suspend fun getAnswerQuestionFlow(): Flow<Question> {
        return cacheDataSource.getAnswerQuestionFlow()
    }


    override suspend fun setQuestionUpdated(isUpdated: Boolean): Resource<Unit> {
        return try {
            localDataSource.setQuestionUpdated(isUpdated)
            cacheDataSource.setQuestionUpdated(isUpdated)
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getQuestionUpdatedFlow(): Flow<Boolean> {
        val local = localDataSource.getQuestionUpdated()
        cacheDataSource.setQuestionUpdated(local)
        return cacheDataSource.getQuestionUpdatedFlow()
    }
}