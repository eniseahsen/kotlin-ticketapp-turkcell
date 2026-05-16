package com.turkcell.data.di

import android.R.attr.level
import com.turkcell.core.domain.AuthRepository
import com.turkcell.data.local.TokenStore
import com.turkcell.data.network.AuthInterceptor
import com.turkcell.data.network.TokenAuthenticator
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.repository.AuthRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://tickets-api.halitkalayci.com/"

private val REFRESH_CLIENT = named("refres_client")
private val REFRESH_RETROFIT = named("refresh_retrofit")
private val REFRESH_API = named("refresh_api")
val dataModule = module{ //bağımlılıkları tanımlamak için
    //scope (kapsam)
    // 3temel seçenek
    //yaşam döngüdndeki bağımlılığın davranış biçimi
    //singelton -> uygulama yaşam döngüsü boyunca tek örnek
    single {
        Json {
            ignoreUnknownKeys = true //cevapta var olan ama classta olmayan alanları ignore et
            explicitNulls = false
            isLenient = true //tırnaksız key gelirse tolere et vs

        }
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    }

    single{
        TokenStore(context=get())
    }//tek bir token store yeterli o yüzden single

    single {
        TokenStore(context=get())}

    single { AuthInterceptor(tokenStore = get())}

    single{
        TokenAuthenticator(
            tokenStore = get(),
            refreshApiProvider = get(REFRESH_API)
        )
    }

    single(REFRESH_CLIENT){
        OkHttpClient.Builder().addInterceptor(get<HttpLoggingInterceptor>()).build()
    }

    single(REFRESH_RETROFIT){
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(REFRESH_CLIENT))
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single(REFRESH_API){
        get<Retrofit>(REFRESH_RETROFIT).create(AuthApi::class.java)
    }



    // HTTP isteklerini yönetmek
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single {
        get<Retrofit>().create(AuthApi:: class.java)
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi =  get(),
            tokenStore = get()
        )
    }

    // factory -> her çağırıldığında yeni instance üretir. her fonksiyon için birer örnek

    // scoped -> Class -> tüm fonksiyonlarına 1 örnek
}