package com.clonect.feeltalk.mvp_domain.repository

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.news.News

interface NewsRepository {

    suspend fun getNewsList(accessToken: String): Resource<List<News>>

}