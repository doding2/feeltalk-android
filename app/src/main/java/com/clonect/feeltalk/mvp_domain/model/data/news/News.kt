package com.clonect.feeltalk.mvp_domain.model.data.news

data class News(
    val id: Long,
    val target: String,
    val content: String,
    val date: String,
    val type: String = NewsType.News.toString()
)
