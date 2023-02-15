package com.clonect.feeltalk.domain.model.data.news

data class News(
    val id: Long,
    val target: String,
    val content: String,
    val date: String,
    val type: String = NewsType.News.toString()
)
