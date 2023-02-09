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
            val cache = cacheDataSource.getTodayQuestion()
            cache?.let { return Resource.Success(cache) }

            val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val date = format.format(Date())
            val local = localDataSource.getTodayQuestion(date)
            local?.let {
                cacheDataSource.saveTodayQuestion(local)
                return Resource.Success(local)
            }

            val remoteQuestionDto = remoteDataSource.getTodayQuestion(accessToken).body()!!
            val remoteQuestion = remoteQuestionDto.toQuestion()
            cacheDataSource.saveTodayQuestion(remoteQuestion)
            return Resource.Success(remoteQuestion)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Resource.Error(e)
        }
    }

}