package com.clonect.feeltalk.mvp_domain.usecase.news

import com.clonect.feeltalk.common.Resource
import com.clonect.feeltalk.mvp_domain.model.data.news.News
import com.clonect.feeltalk.mvp_domain.repository.NewsRepository
import com.clonect.feeltalk.mvp_domain.repository.UserRepository

class GetNewsListUseCase(
    private val userRepository: UserRepository,
    private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(): Resource<List<News>> {
        val result = userRepository.getAccessToken()
        return when (result) {
            is Resource.Success -> newsRepository.getNewsList(result.data)
            is Resource.Error -> Resource.Error(result.throwable)
        }
    }
}