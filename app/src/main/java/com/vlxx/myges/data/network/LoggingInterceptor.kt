package com.vlxx.myges.data.network

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import timber.log.Timber
import java.nio.charset.Charset

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        Timber.d("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Timber.d("Request: ${request.method} ${request.url}")
        request.headers.forEach { (name, value) ->
            Timber.d("Request Header: $name: $value")
        }

        val response = chain.proceed(request)

        Timber.d("Response: ${response.code} ${request.url}")
        response.headers.forEach { (name, value) ->
            Timber.d("Response Header: $name: $value")
        }

        // Log response body WITHOUT consuming it
        val responseBody = response.body
        if (responseBody != null) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body
            val buffer = source.buffer

            val contentType = responseBody.contentType()
            val charset = contentType?.charset(Charset.forName("UTF-8")) ?: Charset.forName("UTF-8")

            if (buffer.size > 0) {
                val bodyString = buffer.clone().readString(charset)
                Timber.d("Response Body: $bodyString")
            }
        }

        Timber.d("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        return response
    }
}
