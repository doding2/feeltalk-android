package com.clonect.feeltalk.mvp_domain.model.data.news

sealed class NewsType {
    object News: NewsType()
    object Chat: NewsType()
    object Official: NewsType()

    override fun toString(): String = when (this) {
        is News -> "news"
        is Chat -> "chat"
        is Official -> "official"
    }

    fun String.toNewsType(): NewsType = when (this.lowercase()) {
        "news" -> News
        "chat" -> Chat
        "official" -> Official
        else -> News
    }
}

