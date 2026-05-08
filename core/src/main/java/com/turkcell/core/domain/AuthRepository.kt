package com.turkcell.core.domain

import kotlinx.coroutines.flow.Flow

//soyut sözleşme: ne yapılacağını belirtir. nasıl yapılacağını değil, neyle çalıştığını söylemez core katmanı
interface AuthRepository {

    val isLoggedIn: Flow<Boolean>
    suspend fun login(email: String, password: String): Result<AuthSession>
    suspend fun register(email: String, password: String): Result<AuthSession>
    suspend fun logout(): Result<Unit>
}