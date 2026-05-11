package com.turkcell.data.di

import android.R.attr.level
import com.turkcell.core.domain.AuthRepository
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.repository.AuthRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://tickets-api.halitkalayci.com/"

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

    // HTTP isteklerini yönetmek
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
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
            authApi =  get()
        )
    }

    // factory -> her çağırıldığında yeni instance üretir. her fonksiyon için birer örnek

    // scoped -> Class -> tüm fonksiyonlarına 1 örnek
}