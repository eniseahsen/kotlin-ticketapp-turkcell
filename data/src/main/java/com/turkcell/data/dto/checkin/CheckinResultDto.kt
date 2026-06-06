package com.turkcell.data.dto.checkin

import com.turkcell.data.dto.event.EventDto
import kotlinx.serialization.Serializable

@Serializable
data class CheckinResultDto(
    val ticketId: String,
    val ticketType: String,
    val event: EventDto,
    val checkedInAt: String
)