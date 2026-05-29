package com.turkcell.core.domain.ticket

import com.turkcell.core.domain.ticket.TicketStatus

data class Ticket(
    val id: String,
    val qrCode: String,
    val status: TicketStatus,
    val ticketTypeId: String
)