package com.turkcell.ticketapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.core.domain.Ticket
import com.turkcell.core.domain.event.Event
import com.turkcell.ticketapp.viewmodel.HomeUiState

import com.turkcell.ticketapp.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel




@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onTicketClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface (modifier= Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 24.dp)) {
            Text("Yaklaşan Etkinlikler")
            Spacer(Modifier.height(8.dp))
            EventsRow(isLoading = state.isEventsLoading, error=state.eventsError, events=state.events)


            Spacer(Modifier.height(8.dp))
            Text("Satın Alınmış Biletler")
            Spacer(Modifier.height(8.dp))
            TicketsColumn(
                isLoading = state.isTicketsLoading,
                error = state.ticketsError,
                tickets = state.tickets,
                onTicketClick = onTicketClick
            )
        }
    }
}

@Composable
private fun TicketsColumn(
    isLoading: Boolean,
    error: String?,
    tickets: List<Ticket>,
    onTicketClick: (String) -> Unit
) {
    when {
        isLoading -> CircularProgressIndicator()
        error != null -> Text(error)
        tickets.isEmpty() -> Text("Henüz biletiniz yok.")
        else -> LazyColumn {
            items(items = tickets, key = { it.id }) { ticket ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                        .clickable { onTicketClick(ticket.id) }
                ) {
                    Text(
                        text = "Bilet #${ticket.id.take(8)}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EventsRow(
    isLoading: Boolean,
    error: String?,
    events: List<Event>
) {
    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)){
                CircularProgressIndicator()
            }
        }
        error != null -> {
            Text(error)
        }
        events.isEmpty() -> {
            Text(text="Şimdilik hiç bir etkinlik yok.", style= MaterialTheme.typography.bodyMedium)
        }
        else -> {
            LazyRow(contentPadding = PaddingValues(horizontal = 24.dp)) {
                items(items=events, key = {it.id}) {event -> EventCard(event)}
            }
        }
    }
}

@Composable
private fun EventCard(event: Event)
{
    Card(
        modifier = Modifier.width(260.dp).height(280.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = event.name.take(1).uppercase().ifBlank { "?" },
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Text(event.name)
            Text(event.venue)
            Text(event.description)
        }
    }
}