package com.clonect.feeltalk.common

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()
}


fun <T> Resource<T>.onSuccess(action: (data: T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        action(data)
    }
    return this
}

fun <T> Resource<T>.onError(action: (throwable: Throwable) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(throwable)
    }
    return this
}