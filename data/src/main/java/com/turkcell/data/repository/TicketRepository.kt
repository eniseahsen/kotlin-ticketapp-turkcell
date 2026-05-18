package com.turkcell.data.repository

import com.turkcell.core.domain.Ticket

interface TicketRepository {
    suspend fun getMyTickets(): Result<List<Ticket>>
}