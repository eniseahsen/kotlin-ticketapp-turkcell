package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.Event
import com.turkcell.core.domain.EventRepository
import com.turkcell.core.domain.Ticket
import com.turkcell.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val tickets: List<Ticket> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository
): ViewModel(){
    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init{
        loadData()
    }

    fun loadData(){
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch{
            val eventsResult = eventRepository.getEvents()
            val ticketResults = ticketRepository.getMyTickets()

            eventsResult
                .onSuccess { events ->
                    ticketResults
                        .onSuccess { tickets ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    events = events,
                                    tickets = tickets
                                )
                            }
                        }
                        .onFailure{ error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = error.message
                                )
                            }
                        }
                }
                .onFailure { error  ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }

    }
}