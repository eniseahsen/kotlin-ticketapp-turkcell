package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.checkin.CheckinRepository
import com.turkcell.core.domain.checkin.CheckinResult
import com.turkcell.core.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckinUiState(
    val isLoading: Boolean = false,
    val result: CheckinResult? = null,
    val error: String? = null
)

class CheckinViewModel(
    private val checkinRepository: CheckinRepository
): ViewModel(){
    private val _state = MutableStateFlow(CheckinUiState())
    val state: StateFlow<CheckinUiState> = _state.asStateFlow()

    fun onQrDetected(qrCode: String){
        // Zaten yükleniyor veya sonuç varsa tekrar tarama yapma
        if(_state.value.isLoading || _state.value.result != null) return

        _state.update { it.copy(isLoading = true, error = null)}
        viewModelScope.launch {
            checkinRepository.scan(qrCode).fold(
                onSuccess = { result ->
                    // Başarılı  ise sonucu state'e yaz -> ekran sonuç kartını gösterir
                    _state.update {  it.copy(isLoading = false, result = result)}
                },
                onFailure = { e->
                    // Hata -> mesajı state'e yaz -> snackbar gösterilir
                    _state.update { it.copy(isLoading = false, error = e.toUserMessage()) }

                }
            )
        }}
        // "Tekrar Tara" butonuna basılınca state'i sıfırla
        fun reset() = _state.update { CheckinUiState() }

        fun consumeError() = _state.update { it.copy(error = null) }

    fun onPermissionDenied() = _state.update {
        it.copy(error = "Kamera izni olmadan QR taranamaz.")
    }



}