package com.turkcell.data.dto.checkin

import kotlinx.serialization.Serializable

@Serializable
data class CheckinRequestDto(val qrCode: String)