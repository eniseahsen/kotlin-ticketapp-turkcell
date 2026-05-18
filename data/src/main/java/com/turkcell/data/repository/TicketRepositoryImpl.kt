package com.turkcell.data.repository

import com.turkcell.core.domain.Ticket
import com.turkcell.core.domain.TicketStatus
import com.turkcell.data.remote.TicketApi
import com.turkcell.data.util.runCatchingApi

class TicketRepositoryImpl(
    private val ticketApi: TicketApi

): TicketRepository {

    override suspend fun getMyTickets(): Result<List<Ticket>> =
        runCatchingApi {
            ticketApi.getMyTickets()
        }.map { ticketDtos ->
            ticketDtos.map { dto ->
                Ticket(
                    id = dto.id,
                    qrCode = dto.qrCode,
                    status = TicketStatus.fromApi(dto.status),
                    ticketTypeId = dto.ticketTypeId


                )
            }
        }
}