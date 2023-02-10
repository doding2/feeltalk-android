package com.clonect.feeltalk.data.repository.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.mapper.toQuestion
import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.repository.QuestionRepository
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.*

class QuestionRepositoryImpl(
    private val remoteDataSource: QuestionRemoteDataSource,
    private val localDataSource: QuestionLocalDataSource,
    private val cacheDataSource: QuestionCacheDataSource
): QuestionRepository {

    override suspend fun getTodayQuestion(accessToken: String): Resource<Question> {
        try {
            val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val date = format.format(Date())

            val cache = cacheDataSource.getTodayQuestion()
            cache?.let {
                if (cache.questionDate != date)
                    return@let
                return Resource.Success(cache)
            }

            val local = localDataSource.getTodayQuestion(date)
            local?.let {
                cacheDataSource.saveTodayQuestion(local)
                return Resource.Success(local)
            }

            val remoteQuestionDto = remoteDataSource.getTodayQuestion(accessToken).body()!!
            val remoteQuestion = remoteQuestionDto.toQuestion().apply {
                questionDate = date
            }
            localDataSource.saveOneQuestion(remoteQuestion)
            cacheDataSource.saveTodayQuestion(remoteQuestion)
            return Resource.Success(remoteQuestion)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }


    override suspend fun sendQuestionAnswer(
        accessToken: String,
        question: Question,
    ): Resource<String> {
        return try {
            val response = remoteDataSource.sendQuestionAnswer(
                accessToken = accessToken,
                question = question.question,
                answer = question.myAnswer!!,
            )

            // TODO 변경내역 캐시랑 로컬에도 업데이트
            localDataSource.saveOneQuestion(question)

            Resource.Success(response.body()!!.status)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}