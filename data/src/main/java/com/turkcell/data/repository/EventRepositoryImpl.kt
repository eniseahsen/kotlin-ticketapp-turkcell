package com.turkcell.data.repository

import com.turkcell.core.domain.Event
import com.turkcell.core.domain.EventRepository
import com.turkcell.core.domain.TicketType
import com.turkcell.data.remote.EventApi
import com.turkcell.data.util.runCatchingApi

class EventRepositoryImpl(
    private val eventApi: EventApi
): EventRepository {
    override suspend fun getEvents(): Result<List<Event>> =
        runCatchingApi{
            eventApi.getEvents()
        }.map { eventDtos ->
            eventDtos.map { dto ->
                Event(
                    id = dto.id,
                    name = dto.name,
                    description = dto.description,
                    venue = dto.venue,
                    startsAt = dto.startsAt,
                    endsAt = dto.endsAt,

                    TicketTypes = dto.ticketTypes.map { ticketDto ->
                        TicketType(
                            id = ticketDto.id,
                            name = ticketDto.name,
                            priceCents = ticketDto.priceCents,
                            capacity = ticketDto.capacity,
                            soldCount = ticketDto.soldCount,
                            remaining = ticketDto.remaining
                        )

                    }
                )

            }
        }
}