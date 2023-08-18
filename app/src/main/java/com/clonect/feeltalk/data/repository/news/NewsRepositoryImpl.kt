package com.clonect.feeltalk.data.repository.news

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.news.News
import com.clonect.feeltalk.domain.repository.NewsRepository
import com.clonect.feeltalk.new_data.api.ClonectService
import com.google.gson.JsonObject
import kotlinx.coroutines.CancellationException
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException

class NewsRepositoryImpl(
    private val clonectService: ClonectService,
): NewsRepository {

    override suspend fun getNewsList(accessToken: String): Resource<List<News>> {
        return try {
            val body = JsonObject().apply {
                addProperty("accessToken", accessToken)
            }
            val response = clonectService.getNewsList(body)
            if (!response.isSuccessful) throw ServerIsDownException(response)
            if (response.body() == null) throw NullPointerException("Response body from server is null.")
            val newsDtoList = response.body()!!

            val newsList = newsDtoList.mapIndexed { index, newsDto ->
                News(id = (index + 1).toLong(), target = "partner", content = newsDto.message, date = newsDto.createAt)
            }

            Resource.Success(newsList)
        } catch (e: CancellationException) {
            throw e
        }
        catch (e: Exception) {
            Resource.Error(e)
        }
    }

}