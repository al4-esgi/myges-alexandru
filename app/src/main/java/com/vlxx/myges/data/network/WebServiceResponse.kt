package com.vlxx.myges.data.network

sealed class WebServiceResponse<out T> {
    data class Success<T>(val data: T) : WebServiceResponse<T>()
    data class Error(val throwable: Throwable) : WebServiceResponse<Nothing>()
}