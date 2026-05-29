package com.turkcell.data.dto.purchase

import com.turkcell.core.domain.purchase.Purchase
import kotlinx.serialization.Serializable

@Serializable
data class CreatePurchaseRequestDto(val items: List<PurchaseItemRequestDto>)