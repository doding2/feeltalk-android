package com.clonect.feeltalk.new_domain.model.page

sealed class  PageEvents<T>(open val item: T) {
    data class Edit<T>(override val item: T): PageEvents<T>(item)
    data class Remove<T>(override val item: T): PageEvents<T>(item)
    data class InsertItemHeader<T>(override val item: T): PageEvents<T>(item)
    data class InsertItemFooter<T>(override val item: T): PageEvents<T>(item)
}
