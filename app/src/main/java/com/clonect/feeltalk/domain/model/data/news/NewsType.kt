package com.clonect.feeltalk.domain.model.data.news

sealed class NewsType {
    object News: NewsType()
    object Chat: NewsType()
    object Official: NewsType()
}
