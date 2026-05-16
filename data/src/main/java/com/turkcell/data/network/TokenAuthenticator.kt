package com.turkcell.data.network

import com.turkcell.data.dto.RefreshRequestDto
import com.turkcell.data.local.TokenStore
import com.turkcell.data.remote.AuthApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

//sadece http 401lerde çalış, refresh akışı sürdür
class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val refreshApiProvider: () -> AuthApi,

    ) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // İstek 401'e düştüğü anda, eğer sistemde jwt-refresh pairi tanımlıysa git refresh ile yeni jwt alıp isteği tekrar dene.
        val refreshToken = tokenStore.refreshTokenBlocking() ?: return null;

        return synchronized(this)
        {
            // Bu blokta birden fazla istek aynı anda 401 alırsa kuyruğa girer..
            // Blok bitene kadar, yeni yapıları beklet..
            val current = tokenStore.accessTokenBlocking()
            val sentToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // Başkası tarafından token değişmise, onu kullan ve devam et..
            if(current != null && current != sentToken){
                return@synchronized response.request.signWith(current)

            }

            val newPair = runCatching {
                runBlocking { refreshApiProvider().refresh(RefreshRequestDto(refreshToken)) }

            }.getOrNull()

            if (newPair == null){
                //refresh başarısız
                tokenStore.clearBlocking()
                return@synchronized null
            }

            tokenStore.saveBlocking(newPair.accessToken, newPair.refreshToken)
            response.request.signWith(newPair.accessToken)


        }

    }

    private fun Request.signWith(accessToken: String): Request = newBuilder().header("Authorization", "Bearer $accessToken").build()

    private fun Response.priorResponseCount(): Int{
        var count = 0
        var prior = priorResponse
        while(prior != null){
            count ++
            prior = prior.priorResponse
        }
        return count
    }

}