package com.turkcell.data.network

import com.turkcell.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {

    private val authPaths = setOf(
        "/auth/login",
        "/auth/register",
        "auth/refresh"

    )

    // her istekte çalışır
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request() //şu anki http isteği
        val path = original.url.encodedPath

        if(path in authPaths) return chain.proceed(original)


        //data storedan access token çekilir
        //token yoksa isteği normal gönder
        val token = tokenStore.accessTokenBlocking() ?: return chain.proceed(original)

        val authedRequest = original
            .newBuilder() //orijinal isteğin klonunu oluşturur
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authedRequest)

    }
}