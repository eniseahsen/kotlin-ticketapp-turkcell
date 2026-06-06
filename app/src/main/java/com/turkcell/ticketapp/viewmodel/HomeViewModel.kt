package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.auth.UserRole
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.ticket.Ticket
import com.turkcell.core.domain.ticket.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isEventsLoading: Boolean = false,
    val isEventsRefreshing: Boolean = false,
    val events: List<Event> = emptyList(),
    val eventsError: String? = null,
    val isTicketsLoading: Boolean = false,
    val isTicketsRefreshing: Boolean = false,
    val tickets: List<Ticket> = emptyList(),
    val ticketsError: String? = null,
    val userRole: UserRole = UserRole.USER
)

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadEvents()
        loadTickets()
        observeUserRole()
    }

    private fun observeUserRole(){
        viewModelScope.launch{
            authRepository.userRole.collect { role ->
                _state.update { it.copy(userRole = role) }
            }
        }
    }

    fun loadEvents() {
        if (_state.value.isEventsLoading) return
        _state.update { it.copy(isEventsLoading = true, eventsError = null) }
        fetchEvents()
    }

    fun loadTickets() {
        if (_state.value.isTicketsLoading) return
        _state.update { it.copy(isTicketsLoading = true, ticketsError = null) }
        fetchTickets()
    }

    fun refreshAll() {
        if (_state.value.isEventsRefreshing) return
        _state.update { it.copy(isEventsRefreshing = true, isTicketsRefreshing = true, eventsError = null, ticketsError = null) }
        fetchEvents()
        fetchTickets()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            eventRepository.getEvents().fold(
                onSuccess = { list ->
                    _state.update { it.copy(events = list, isEventsLoading = false, isEventsRefreshing = false, eventsError = null) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isEventsLoading = false, isEventsRefreshing = false, eventsError = e.message ?: "Etkinlikler yüklenemedi.") }
                }
            )
        }
    }

    private fun fetchTickets() {
        viewModelScope.launch {
            ticketRepository.getMyTickets().fold(
                onSuccess = { list ->
                    _state.update { it.copy(tickets = list, isTicketsLoading = false, isTicketsRefreshing = false) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isTicketsLoading = false, isTicketsRefreshing = false, ticketsError = e.message ?: "Biletler yüklenemedi.") }
                }
            )
        }
    }
}