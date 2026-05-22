package com.turkcell.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class CredentialsDto(val email: String, val password: String) {
}