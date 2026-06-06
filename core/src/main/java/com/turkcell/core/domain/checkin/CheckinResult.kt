package com.turkcell.core.domain.checkin

import com.turkcell.core.domain.event.Event

data class CheckinResult(
    val ticketId: String,
    val ticketType: String,
    val event: Event,
    val checkedInAt: String

    )