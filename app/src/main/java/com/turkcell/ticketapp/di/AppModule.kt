package com.turkcell.ticketapp.di

import org.koin.androidx.viewmodel.dsl.viewModel
import com.turkcell.core.domain.AuthRepository
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.ticketapp.ui.login.LoginViewModel
import org.koin.dsl.module

val appModule = module {
    single<AuthApi> {
        TODO()
    }

    single<AuthRepository>{
        AuthRepositoryImpl(get()) //AuthApi'yi Koin'den al
    }

    viewModel{
        LoginViewModel(get())
    }
}