package com.turkcell.core.exception

// Bağlantı kopuk, timeout, DNS çözümleme hatası
class NetworkException(cause: Throwable) : RuntimeException("Network Error", cause)

// 4xx (istemci hatası) / 5xx (sunucu hatası) — errorCode backend'in döndürdüğü makine-okunur kod (örn. "email_taken")
class ApiException(
    val code: Int,
    val errorMessage: String?, // HTTP status message
    val errorCode: String? = null, // Backend JSON body'sindeki hata kodu (örn. "email_taken")
    cause: Throwable? = null
) : RuntimeException("HTTP $code: $errorMessage", cause)