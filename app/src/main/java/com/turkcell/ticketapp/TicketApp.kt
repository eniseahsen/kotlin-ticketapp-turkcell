package com.turkcell.ticketapp

import android.app.Application
import com.turkcell.ticketapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class TicketApp : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@TicketApp)
            modules(appModule)
        }
    }
}