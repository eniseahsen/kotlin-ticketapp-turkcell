package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.ticket.Ticket
import com.turkcell.core.util.toUserMessage
import com.turkcell.core.domain.ticket.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyTicketUiState(
    val isLoading: Boolean = false,
    val tickets: List<Ticket> = emptyList(),
    val error: String? = null
)

class MyTicketViewModel(
    private val ticketRepository: TicketRepository
): ViewModel(){

    private val _state = MutableStateFlow(MyTicketUiState())
    val state: StateFlow<MyTicketUiState> = _state.asStateFlow()

    init{ loadTickets()}

    fun loadTickets(){
        if(_state.value.isLoading) return
        _state.update { it.copy(isLoading = true, error = null)}
        viewModelScope.launch {
            ticketRepository.getMyTickets().fold(
                onSuccess = { list ->
                    _state.update { it.copy(isLoading = false, tickets = list)}
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.toUserMessage())}
                }
            )
        }
    }

}