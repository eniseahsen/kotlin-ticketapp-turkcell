package com.turkcell.core.domain.auth

enum class UserRole { //enumerated: liste içinden sadece
    USER, STAFF, ADMIN;

    companion object{ //enumın içerisinde fun yazabilmke için ekstra syntax
        fun fromApi(value: String?): UserRole = when (value?.uppercase()){
            "ADMIN" -> UserRole.ADMIN
            "STAFF" -> UserRole.STAFF
            else -> UserRole.USER
        }
    }


}