package com.clonect.feeltalk.data.repository.question

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.data.mapper.toQuestion
import com.clonect.feeltalk.data.mapper.toQuestionList
import com.clonect.feeltalk.data.repository.question.datasource.QuestionCacheDataSource2
import com.clonect.feeltalk.data.repository.question.datasource.QuestionLocalDataSource2
import com.clonect.feeltalk.data.repository.question.datasource.QuestionRemoteDataSource2
import com.clonect.feeltalk.data.utils.UserLevelEncryptHelper
import com.clonect.feeltalk.domain.model.data.question.Question2
import com.clonect.feeltalk.domain.model.dto.question.QuestionAnswersDto
import com.clonect.feeltalk.domain.model.dto.question.QuestionDetailDto
import com.clonect.feeltalk.domain.model.dto.question.SendQuestionDto
import com.clonect.feeltalk.domain.model.dto.question.TodayQuestionAnswersDto
import com.clonect.feeltalk.domain.repository.QuestionRepository2
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class QuestionRepository2Impl(
    private val remoteDataSource: QuestionRemoteDataSource2,
    private val localDataSource: QuestionLocalDataSource2,
    private val cacheDataSource: QuestionCacheDataSource2,
    private val userLevelEncryptHelper: UserLevelEncryptHelper
): QuestionRepository2 {

    override suspend fun getTodayQuestion(accessToken: String): Resource<Question2> {
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

            if (yesterdayQuestion == null) {
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
        question2: Question2,
    ): Resource<SendQuestionDto> {
        return try {
            val encrypted = userLevelEncryptHelper.encryptMyText(question2.myAnswer!!)
            val response = remoteDataSource.sendQuestionAnswer(
                accessToken = accessToken,
                question = question2.question,
                answer = encrypted,
            )

            localDataSource.saveOneQuestion(question2)

            val cacheTodayQuestion = cacheDataSource.getTodayQuestion()
            if (cacheTodayQuestion?.question == question2.question) {
                cacheDataSource.saveTodayQuestion(question2)
            } else {
                cacheDataSource.saveOneQuestion(question2)
            }

            Resource.Success(response.body()!!)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    @OptIn(FlowPreview::class)
    override suspend fun getQuestionList(accessToken: String): Flow<Resource<List<Question2>>> {
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
                val questionList = dto.toQuestionList()

                localDataSource.saveQuestionList(questionList)
                cacheDataSource.saveQuestionList(questionList)

//                send(Resource.Success(questionList))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                send(Resource.Error(e))
            }
        }

        return flowOf(cacheFlow, localFlow, remoteFlow).flattenMerge()
    }

    override suspend fun getQuestionByContentFromDB(question: String): Resource<Question2> {
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

    override suspend fun getQuestionAnswers(
        accessToken: String,
        question: String,
    ): Resource<QuestionAnswersDto> {
        return try {
            val remote = remoteDataSource.getQuestionAnswers(accessToken, question).body()!!
            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun saveQuestionToDatabase(question2: Question2): Resource<Long> {
        return try {
            val local = localDataSource.saveOneQuestion(question2)
            Resource.Success(local)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }


    override suspend fun getTodayQuestionAnswersFromServer(accessToken: String): Resource<TodayQuestionAnswersDto> {
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

    override suspend fun getQuestionDetail(
        accessToken: String,
        question: String,
    ): Resource<QuestionDetailDto> {
        return try {
            val remote = remoteDataSource.getQuestionDetail(accessToken, question).body()!!
            Resource.Success(remote)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

}