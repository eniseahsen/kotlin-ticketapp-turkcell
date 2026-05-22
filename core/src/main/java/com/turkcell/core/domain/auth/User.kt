package com.turkcell.core.domain.auth

import com.turkcell.core.domain.auth.UserRole

data class User(val id: String, val email: String, val role: UserRole) {



}