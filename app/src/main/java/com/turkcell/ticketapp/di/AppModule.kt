package com.turkcell.ticketapp.di

import org.koin.androidx.viewmodel.dsl.viewModel
import com.turkcell.core.domain.AuthRepository
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.ticketapp.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    //viewModel
    viewModelOf(::LoginViewModel)

}