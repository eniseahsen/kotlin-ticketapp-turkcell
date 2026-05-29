package com.turkcell.data.dto.purchase


import com.turkcell.data.dto.event.TicketDto
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseDto(
    val id: String,
    val status: String,
    val totalCents: Long,
    val paidAt: String? = null,
    val items: List<PurchaseItemDto>,
    val tickets: List<TicketDto> = emptyList()

)