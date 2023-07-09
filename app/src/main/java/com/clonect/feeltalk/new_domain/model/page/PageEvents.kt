package com.clonect.feeltalk.new_domain.model.page

sealed class  PageEvents<T> {
    data class Edit<T>(val item: T): PageEvents<T>()
    data class Remove<T>(val item: T): PageEvents<T>()
    data class InsertItemHeader<T>(val item: T): PageEvents<T>()
    data class InsertItemFooter<T>(val item: T): PageEvents<T>()
}
