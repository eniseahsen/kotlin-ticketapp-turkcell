package com.turkcell.data.dto.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseItemDto(
    val ticketTypeId: String,
    val quantity: Int
)