package com.vlxx.myges.data.network

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        Timber.d("Request: ${request.method} ${request.url}")
        request.headers.forEach { (name, value) ->
            Timber.d("Header: $name: $value")
        }

        val response = chain.proceed(request)

        Timber.d("Response: ${response.code} ${request.url}")
        response.headers.forEach { (name, value) ->
            Timber.d("Response Header: $name: $value")
        }

        return response
    }
}
