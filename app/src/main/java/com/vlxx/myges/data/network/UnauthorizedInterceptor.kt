package com.vlxx.myges.data.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class UnauthorizedInterceptor : Interceptor {

    var onUnauthorized: (suspend () -> Unit)? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Si on reçoit une erreur 401, on déconnecte l'utilisateur
        if (response.code == 401) {
            // Lancer la déconnexion de manière asynchrone
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    onUnauthorized?.invoke()
                } catch (e: Exception) {
                    // Log l'erreur mais ne bloque pas
                    e.printStackTrace()
                }
            }
        }

        return response
    }
}
