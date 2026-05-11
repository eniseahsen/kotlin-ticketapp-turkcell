package com.turkcell.data.dto

import kotlinx.serialization.Serializable


// backendten gelen veriyi core a aktarmak için dto.
//backendten ham olarak gele Json' temsli eder
@Serializable
data class UserDto(val id: String, val email: String, val role: String){
}