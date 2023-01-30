package com.clonect.feeltalk.common

sealed class Resource<out T> {
    data class Loading(val isLoading: Boolean) : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()
}