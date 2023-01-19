package com.clonect.feeltalk.domain.model.news

sealed class NewsType {
    object News: NewsType()
    object Chat: NewsType()
    object Official: NewsType()
}
