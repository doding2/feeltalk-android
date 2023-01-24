package com.clonect.feeltalk.common

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    object UnLoading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()
}