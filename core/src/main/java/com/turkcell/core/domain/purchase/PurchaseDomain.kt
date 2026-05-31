package com.turkcell.core.domain.purchase

import com.turkcell.core.domain.ticket.Ticket

data class PurchaseItem(
    val ticketTypeId: String,
    val quantity: Int
)

enum class PurchaseStatus{
    PENDING, PAID;

    companion object{
        fun fromApi(value: String?): PurchaseStatus =
            if (value?.uppercase() == "PAID") PAID else PENDING
    }
}

data class Purchase(
    val id: String,
    val status: PurchaseStatus,
    val totalCents: Long,
    val paidAt: String? = null,
    val items: List<PurchaseItem>,
    val tickets: List<Ticket> = emptyList()


)