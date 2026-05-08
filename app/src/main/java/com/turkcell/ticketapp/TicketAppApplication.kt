package com.turkcell.ticketapp

import android.app.Application
import com.turkcell.ticketapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

//uygulama başladığındaactivitylerden önce oluşturulur
//singelton olarak (tek bir nesne) memoryde kalır
//uygulama kapananakadar yok edilmez
class TicketAppApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        //dependency injection
        startKoin {
            androidLogger()
            androidContext(this@TicketAppApplication)
            modules(appModule)

        }
    }
}