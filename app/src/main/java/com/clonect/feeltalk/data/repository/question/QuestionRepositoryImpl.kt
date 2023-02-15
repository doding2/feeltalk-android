package com.clonect.feeltalk.data.repository.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.mapper.toQuestion
import com.clonect.feeltalk.data.mapper.toQuestionList
import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.model.data.question.Question
import com.clonect.feeltalk.domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.clonect.feeltalk.domain.repository.QuestionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class QuestionRepositoryImpl(
    private val remoteDataSource: QuestionRemoteDataSource,
    private val localDataSource: QuestionLocalDataSource,
    private val cacheDataSource: QuestionCacheDataSource,
    private val userLevelEncryptHelper: UserLevelEncryptHelper
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
            
            // 어제자 질문이랑 오늘 서버의 질문이랑 같은지 체크
            val yesterday = Date(System.currentTimeMillis()-24*60*60*1000)
            val yesterdayDate = format.format(yesterday)
            val yesterdayQuestion = localDataSource.getTodayQuestion(yesterdayDate)

            if (yesterdayQuestion?.question == null) {
                localDataSource.saveOneQuestion(remoteQuestion)
                cacheDataSource.saveTodayQuestion(remoteQuestion)
                return Resource.Success(remoteQuestion)
            }

            if (yesterdayQuestion.question == remoteQuestion.question) {
                throw Exception("Today question of server is not updated yet")
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
    ): Resource<SendQuestionDto> {
        return try {
            val encrypted = userLevelEncryptHelper.encryptMyText(question.myAnswer!!)
            val response = remoteDataSource.sendQuestionAnswer(
                accessToken = accessToken,
                question = question.question,
                answer = encrypted,
            )

            localDataSource.saveOneQuestion(question)

            val cacheTodayQuestion = cacheDataSource.getTodayQuestion()
            if (cacheTodayQuestion?.question == question.question) {
                cacheDataSource.saveTodayQuestion(question)
            } else {
                cacheDataSource.saveOneQuestion(question)
            }

            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    @OptIn(FlowPreview::class)
    override suspend fun getQuestionList(accessToken: String): Flow<Resource<List<Question>>> {
        val cacheFlow = channelFlow {
            val cache = cacheDataSource.getQuestionList()
            send(Resource.Success(cache))
        }

        val localFlow = channelFlow {
            val local = localDataSource.getQuestionListFlow()
            local.collectLatest {
                cacheDataSource.saveQuestionList(it)
                send(Resource.Success(it))
            }
        }

        val remoteFlow = channelFlow {
            try {
                val response = remoteDataSource.getQuestionList(
                    accessToken = accessToken
                )
                val dto = response.body()!!
                val questionList = dto.toQuestionList().map { question ->
                    val decrypted = question.myAnswer?.let { userLevelEncryptHelper.decryptMyText(it) }
                    question.copy(myAnswer = decrypted)
                }

                localDataSource.saveQuestionList(questionList)
                cacheDataSource.saveQuestionList(questionList)

                send(Resource.Success(questionList))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                send(Resource.Error(e))
            }
        }

        return flowOf(cacheFlow, localFlow, remoteFlow).flattenMerge()
    }

    override suspend fun getQuestionByContentFromDB(question: String): Resource<Question> {
        return try {
            val local = localDataSource.getQuestionByContent(question)
                ?: throw NullPointerException("Question is not saved in database: ${question}")
            Resource.Success(local)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun saveQuestionToDatabase(question: Question): Resource<Long> {
        return try {
            val local = localDataSource.saveOneQuestion(question)
            Resource.Success(local)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun getTodayQuestionAnswersFromServer(accessToken: String): Resource<QuestionAnswersDto> {
        return try {
            val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val date = format.format(Date())

            val remote = remoteDataSource.getTodayQuestionAnswers(accessToken).body()!!

            val cache = cacheDataSource.getTodayQuestion()
            cache?.let { question ->
                question.myAnswer = remote.myAnswer?.let { userLevelEncryptHelper.decryptMyText(it) }
                question.partnerAnswer = remote.partnerAnswer?.let { userLevelEncryptHelper.decryptPartnerText(it) }
                cacheDataSource.saveTodayQuestion(question)
            }

            val local = localDataSource.getTodayQuestion(date)
            local?.let { question ->
                question.myAnswer = remote.myAnswer?.let { userLevelEncryptHelper.decryptMyText(it) }
                question.partnerAnswer = remote.partnerAnswer?.let { userLevelEncryptHelper.decryptPartnerText(it) }
                localDataSource.saveOneQuestion(question)
                cacheDataSource.saveTodayQuestion(question)
            }

            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

}