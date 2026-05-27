package com.turkcell.core.util

import com.turkcell.core.domain.exception.ApiException
import com.turkcell.core.domain.exception.NetworkException


fun Throwable.toUserMessage(): String = when (this){
    is ApiException -> when (code) {
        401 -> "Email veya şifre hatalı"
        403 -> when (errorCode?.lowercase()){
            "not_purchase_owner" -> "Bu siparişin sahibi değilsiniz"
            else                 -> "Bu işlemi yapmaya yetkiniz yok"

        }
        404 -> "İçerik bulunamadı"
        409 -> when(errorCode?.lowercase()){
            "email_taken" -> "Bu email zaten kayıtlı"
            "capacity_exceeded" -> "Stok yetersiz, lütfen sayfasyı yenileyin"
            "already_paid" -> "Bu sipariş zaten ödenmiş"
            else           -> "Çakışma hatası oluştu"
        }
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Beklenmeyen bir hata oluştu"

    }
    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu"
}