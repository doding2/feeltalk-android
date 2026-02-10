package com.clonect.feeltalk.release_data.repository.chat.paging

import android.content.Context
import android.graphics.BitmapFactory
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.release_data.mapper.toChatList
import com.clonect.feeltalk.release_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.release_domain.model.challenge.Challenge
import com.clonect.feeltalk.release_domain.model.chat.Chat
import com.clonect.feeltalk.release_domain.model.chat.ChatListDto
import com.clonect.feeltalk.release_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.release_domain.model.question.Question
import com.clonect.feeltalk.release_domain.repository.challenge.ChallengeRepository
import com.clonect.feeltalk.release_domain.repository.question.QuestionRepository
import com.clonect.feeltalk.release_domain.repository.token.TokenRepository
import com.clonect.feeltalk.release_presentation.ui.util.dpToPx
import com.clonect.feeltalk.mvp_presentation.utils.infoLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class ChatPagingSource(
    private val context: Context,
    private val tokenRepository: TokenRepository,
    private val questionRepository: QuestionRepository,
    private val challengeRepository: ChallengeRepository,
    private val chatRemoteDataSource: ChatRemoteDataSource,
): PagingSource<Long, Chat>() {

    override fun getRefreshKey(state: PagingState<Long, Chat>): Long? {
        val page: Long? = runBlocking(Dispatchers.IO) {
            try {
                val result = getLastChatPageNo()
                result.pageNo
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                infoLog("chat paging error in getRefreshKey(): ${e.localizedMessage}\n${e.stackTrace.joinToString("\n")}")
                null
            }
        }
        return page
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Chat> {
        return try {
            val pageKey = params.key
                ?: getLastChatPageNo().pageNo

            val result = getChatList(pageKey).toChatList(
                loadQuestion = ::getQuestion,
                loadChallenge = ::getChallenge,
                loadImage = ::preloadImage
            )
            LoadResult.Page(
                data = result,
                prevKey = if (pageKey <= 0) null else pageKey - 1,
                nextKey = null
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            infoLog("chat paging error in load(): ${e.localizedMessage}")
            LoadResult.Error(e)
        }
    }


    private suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        val tokenInfo = tokenRepository.getTokenInfo()
        if (tokenInfo is Resource.Error) {
            throw tokenInfo.throwable
        }
        (tokenInfo as Resource.Success).data.accessToken
    }

    private suspend fun getLastChatPageNo(): LastChatPageNoDto = withContext(Dispatchers.IO) {
        chatRemoteDataSource.getLastChatPageNo(getAccessToken())
    }

    private suspend fun getChatList(pageNo: Long): ChatListDto = withContext(Dispatchers.IO) {
        chatRemoteDataSource.getChatList(getAccessToken(), pageNo)
    }

    private suspend fun getQuestion(index: Long): Question = withContext(Dispatchers.IO) {
        val result = questionRepository.getQuestion(getAccessToken(), index)
        if (result is Resource.Error) {
            throw result.throwable
        }
        (result as Resource.Success).data
    }

    private suspend fun getChallenge(index: Long): Challenge = withContext(Dispatchers.IO) {
        val result = challengeRepository.getChallenge(getAccessToken(), index)
        if (result is Resource.Error) {
            throw result.throwable
        }
        (result as Resource.Success).data
    }

    private suspend fun preloadImage(index: Long, url: String): Triple<File?, Int, Int> {
        val local = File(context.cacheDir, "${index}.png")
        if (local.exists() && local.canRead()) {
            val bitmap = BitmapFactory.decodeFile(local.path)

            val width = bitmap.width
            val height = bitmap.height

            val mWidth = calculateWidth(context, width, height)
            val mHeight = calculateHeight(context, width, height)

            return Triple(local, mWidth, mHeight)
        }
        return chatRemoteDataSource.preloadImage(index, url)
    }

    private fun calculateWidth(context: Context, width: Int, height: Int): Int {
        // resize by ratio and scale
        val maxWidth = context.dpToPx(252f).toFloat()
        val maxHeight = context.dpToPx(300f).toFloat()

        val mWidth = width.toFloat().takeIf { it > 0 } ?: return maxWidth.toInt()
        val mHeight = height.toFloat().takeIf { it > 0 } ?: return maxWidth.toInt()

        val maxRatio = maxWidth / maxHeight
        val imageRatio = mWidth / mHeight

        return if (imageRatio > maxRatio) {
            maxWidth
        } else {
            mWidth * (maxHeight / mHeight)
        }.toInt()
    }


    private fun calculateHeight(context: Context, width: Int, height: Int): Int {
        // resize by ratio and scale
        val maxWidth = context.dpToPx(252f).toFloat()
        val maxHeight = context.dpToPx(300f).toFloat()

        val mWidth = width.toFloat().takeIf { it > 0 } ?: return maxHeight.toInt()
        val mHeight = height.toFloat().takeIf { it > 0 } ?: return maxHeight.toInt()

        val maxRatio = maxWidth / maxHeight
        val imageRatio = mWidth / mHeight

        return if (imageRatio > maxRatio) {
            mHeight * (maxWidth / mWidth)
        } else {
            maxHeight
        }.toInt()
    }
}