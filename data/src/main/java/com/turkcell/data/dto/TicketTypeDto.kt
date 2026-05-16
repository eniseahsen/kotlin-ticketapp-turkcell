package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketTypeDto(val id: String, val name: String, val priceCents: Int, val capacity: Int, val soldCount: Int, val remaining: Int)