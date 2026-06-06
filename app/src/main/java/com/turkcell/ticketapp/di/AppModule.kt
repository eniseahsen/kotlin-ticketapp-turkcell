package com.turkcell.ticketapp.di

import org.koin.androidx.viewmodel.dsl.viewModel
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.ticketapp.viewmodel.CheckinViewModel
import com.turkcell.ticketapp.viewmodel.EventDetailViewModel
import com.turkcell.ticketapp.viewmodel.HomeViewModel
import com.turkcell.ticketapp.viewmodel.LoginViewModel
import com.turkcell.ticketapp.viewmodel.MyTicketViewModel
import com.turkcell.ticketapp.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.turkcell.ticketapp.viewmodel.PurchaseViewModel
import com.turkcell.ticketapp.viewmodel.TicketDetailViewModel


val appModule = module {
    //viewModel
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::EventDetailViewModel)
    viewModelOf(::PurchaseViewModel)
    viewModelOf(::MyTicketViewModel)
    viewModelOf(::TicketDetailViewModel)
    viewModelOf(::CheckinViewModel)



}