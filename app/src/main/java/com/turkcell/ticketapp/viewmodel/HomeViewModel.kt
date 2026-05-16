package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.Event
import com.turkcell.core.domain.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState (
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val eventRepository: EventRepository
): ViewModel(){
    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    init{
        loadEvents()
    }

    fun loadEvents(){
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            eventRepository.getEvents()
                .onSuccess { events ->
                    _state.update{
                        it.copy(
                            isLoading = false,
                            events = events

                        )
                    }
                }
                .onFailure { error ->
                    _state.update{
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }
}