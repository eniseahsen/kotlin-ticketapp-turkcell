package com.turkcell.data.network
//bağlantı kopuk, timeout, dns çözümleme
class NetworkException(cause: Throwable) : RuntimeException("Network Error", cause)

// sunucu 4xx, 5xx
class ApiException(
    val code: Int,
    val errorMessage: String?,
    cause: Throwable? = null
) : RuntimeException("HTTP $code: $errorMessage", cause)
