package com.turkcell.data.mapper

import com.turkcell.core.domain.checkin.CheckinResult
import com.turkcell.core.domain.ticket.Ticket
import com.turkcell.core.domain.ticket.TicketStatus
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.TicketType
import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseItem
import com.turkcell.core.domain.purchase.PurchaseStatus
import com.turkcell.data.dto.checkin.CheckinResultDto
import com.turkcell.data.dto.event.TicketTypeDto
import com.turkcell.data.dto.event.EventDto
import com.turkcell.data.dto.event.TicketDto
import com.turkcell.data.dto.purchase.PurchaseDto

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

internal fun TicketDto.toDomain(): Ticket = Ticket(
    id = id,
    qrCode = qrCode,
    status = TicketStatus.fromApi(status),
    ticketTypeId = ticketTypeId

)

internal fun PurchaseDto.toDomain(): Purchase = Purchase(
    id = id,
    status = PurchaseStatus.fromApi(status),
    totalCents = totalCents,
    paidAt = paidAt,
    items = items.map { PurchaseItem(it.ticketTypeId, it.quantity) },
    tickets = tickets.map { it.toDomain()}

)

internal fun CheckinResultDto.toDomain(): CheckinResult = CheckinResult(
    ticketId = ticketId,
    ticketType = ticketType,
    event = event.toDomain(),
    checkedInAt = checkedInAt
)