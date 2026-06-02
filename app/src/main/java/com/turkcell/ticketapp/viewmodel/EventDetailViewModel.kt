package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.util.toUserMessage
import com.turkcell.ticketapp.navigation.EventDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val event: Event? = null,
    val error: String? = null,
    val selectedQuantities: Map<String, Int> = emptyMap()
){
    val totalCents: Long
        get() = event?.ticketTypes?.sumOf { tt ->
            tt.priceCents * (selectedQuantities[tt.id] ?: 0)
        } ?: 0L
    val canPurchase: Boolean
        get() = totalCents > 0
}

class EventDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventRepository: EventRepository
) : ViewModel(){
    private val eventId: String = savedStateHandle.toRoute<EventDetail>().id

    private val _state = MutableStateFlow(EventDetailUiState())
    val state: StateFlow<EventDetailUiState> = _state.asStateFlow()

    init {
        loadEvent()
    }

    fun loadEvent(){
        if (_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, error = null) }
        fetchEvent()
    }

    fun refreshEvent(){
        if (_state.value.isRefreshing) return
        _state.update { it.copy(isRefreshing = true, error = null) }
        fetchEvent()
    }

    private fun fetchEvent(){
        viewModelScope.launch {
            eventRepository.getEvent(eventId).fold(
                onSuccess = { event ->
                    _state.update { it.copy(isLoading = false, isRefreshing = false, event = event, selectedQuantities = emptyMap()) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, isRefreshing = false, error = e.toUserMessage()) }
                }
            )
        }
    }

    fun increaseQuantity(ticketTypeId: String){
        val event = _state.value.event ?: return
        val tt = event.ticketTypes.find { it.id == ticketTypeId } ?: return
        val max = minOf(20, tt.remaining.toInt())
        val current = _state.value.selectedQuantities[ticketTypeId] ?: 0
        if (current >= max) return
        _state.update { s ->
            s.copy(selectedQuantities = s.selectedQuantities + (ticketTypeId to current + 1))
        }
    }

    fun decreaseQuantity(ticketTypeId: String){
        val current = _state.value.selectedQuantities[ticketTypeId] ?: 0
        if (current <= 0) return
        _state.update { s ->
            val newQty = current - 1
            val updated = if (newQty == 0)
                s.selectedQuantities - ticketTypeId
            else
                s.selectedQuantities + (ticketTypeId to newQty)
            s.copy(selectedQuantities = updated)
        }
    }
}