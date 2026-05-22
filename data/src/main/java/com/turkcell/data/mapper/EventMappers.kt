package com.turkcell.data.mapper

import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.TicketType
import com.turkcell.data.dto.event.TicketTypeDto
import com.turkcell.data.dto.event.EventDto

internal fun EventDto.toDomain(): Event = Event(
    id= id,
    name= name,
    description= description.orEmpty(),
    venue= place.orEmpty(),
    startsAt = startsAt.orEmpty(),
    endsAt = endsAt,
    ticketTypes = ticketTypes.map { it.toDomain() }
)

internal fun TicketTypeDto.toDomain() : TicketType = TicketType(
    id=id,
    name=name,
    priceCents=priceCents,
    capacity=capacity,
    soldCount=soldCount,
    remaining=remaining
)