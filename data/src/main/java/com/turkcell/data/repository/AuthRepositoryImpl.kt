package com.turkcell.data.repository

import com.turkcell.core.domain.AuthRepository
import com.turkcell.core.domain.AuthSession
import com.turkcell.core.domain.User
import com.turkcell.core.domain.UserRole
import com.turkcell.data.dto.CredentialsDto
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.util.runCatchingApi
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {
    override val isLoggedIn: Flow<Boolean>
        get() = TODO("Not yet implemented")

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.login(CredentialsDto(email=email, password=password))
    }.onSuccess {
        // jwt'i bi yere yaz..
    }
        .map {
                tokenPairDto -> AuthSession(
            user = User(
                tokenPairDto.user.id, tokenPairDto.user.email, UserRole.fromApi(tokenPairDto.user.role),
            ),
            accessToken = tokenPairDto.accessToken,
            refreshToken = tokenPairDto.refreshToken)
        }


    override suspend fun register(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi{
        authApi.register(CredentialsDto(email = email, password = password))

    }.map { tokenPairDto ->
        AuthSession(
            user = User(
                id = tokenPairDto.user.id,
                email = tokenPairDto.user.email,
                role = UserRole.fromApi(tokenPairDto.user.role),
            ),
            accessToken = tokenPairDto.accessToken,
            refreshToken = tokenPairDto.refreshToken
        )
    }

    override suspend fun logout(): Result<Unit> {
        TODO("Not yet implemented")
    }
}