package com.turkcell.core.domain.ticket

enum class TicketStatus {
    VALID,
    USED,
    CANCELLED;

    companion object{
        fun fromApi(value: String?): TicketStatus =
            when(value?.uppercase()){
                "USED" -> USED
                "CANCELLED" -> CANCELLED
                else -> VALID
            }
    }
}