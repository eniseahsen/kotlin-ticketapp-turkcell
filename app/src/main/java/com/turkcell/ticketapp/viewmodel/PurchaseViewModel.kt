package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.purchase.PurchaseItem
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.core.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PurchaseUiState(
    val isLoading: Boolean = false,
    val pendingPurchaseId: String? = null,
    val isPaid: Boolean = false,
    val errorMessage: String? = null,
    val shouldRefreshEvent: Boolean = false
)

class PurchaseViewModel(
    private val purchaseRepository: PurchaseRepository
): ViewModel(){

    private val _state = MutableStateFlow(PurchaseUiState())
    val state: StateFlow<PurchaseUiState> = _state.asStateFlow()

    fun createPurchase(selectedQuantities: Map<String, Int>){
        // Map'i domain modeline çeviriyoruz
        // quantity 0 olan ticket type'ları filtreliyoruz
        val items = selectedQuantities
            .filter { it.value > 0}
            .map { (typeId, qty) -> PurchaseItem(typeId, qty) }
        if (items.isEmpty()) return

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch{
            purchaseRepository.createPurchase(items).fold(
                onSuccess = { purchase ->
                    // Başarılı → purchase.id'yi saklıyoruz
                    // pendingPurchaseId set edilince ekran diyaloğu açıyor
                    _state.update { it.copy(isLoading = false, pendingPurchaseId = purchase.id) }


                },
                onFailure = { e ->
                    val msg = e.toUserMessage()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = msg,
                            // "Stok yetersiz" mesajı geldiyse etkinliği yenilememiz lazım
                            shouldRefreshEvent = msg.contains("Stok yetersiz")
                        )
                    }
                }
            )
        }
    }

    fun confirmPayment(){
        // pendingPurchaseId yoksa zaten çağrılmamalı ama guard koyuyoruz
        val purchaseId = _state.value.pendingPurchaseId ?: return

        _state.update { it.copy(isLoading = true, errorMessage = null)}

        viewModelScope.launch {
            purchaseRepository.pay(purchaseId).fold(
                onSuccess = {
                    // isPaid = true → ekran Biletlerim'e navigate ediyor
                    _state.update { it.copy(isLoading = false, isPaid = true, pendingPurchaseId = null)}
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.toUserMessage()) }

                }
            )
        }
    }

    // Diyalog "İptal"e basılınca pendingPurchaseId'yi temizle → diyalog kapanır
    fun dismissDialog() = _state.update{ it.copy(pendingPurchaseId = null)}

    // Snackbar gösterildikten sonra mesajı temizle → tekrar gösterilmesin
    fun consumeError() = _state.update {it.copy(errorMessage = null)}

    // EventDetailScreen refresh ettikten sonra flag'i sıfırla
    fun consumeRefreshEvent() = _state.update { it.copy(shouldRefreshEvent = false)}




}