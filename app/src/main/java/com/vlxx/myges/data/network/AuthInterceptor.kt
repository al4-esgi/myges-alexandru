package com.vlxx.myges.data.network

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private var token: String? = null) : Interceptor {

    fun setToken(newToken: String?) {
        token = newToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // If token exists and it's not the auth endpoint, add Bearer token
        if (token != null && !request.url.toString().contains("authentication.kordis.fr")) {
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("User-Agent", "Skolae/2.0 (iPhone; iOS 14.0; Scale/3.00)")
                .header("Accept", "application/json")
                .header("Accept-Language", "fr-FR,fr;q=0.9")
                .build()
            return chain.proceed(authenticatedRequest)
        }

        return chain.proceed(request)
    }

    companion object {
        fun createBasicAuth(username: String, password: String): String {
            val credentials = "$username:$password"
            val encoded = Base64.encodeToString(credentials.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            return "Basic $encoded"
        }
    }
}
