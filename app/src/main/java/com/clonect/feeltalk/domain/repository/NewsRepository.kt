package com.clonect.feeltalk.domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.domain.model.data.news.News

interface NewsRepository {

    suspend fun getNewsList(accessToken: String): Resource<List<News>>

}