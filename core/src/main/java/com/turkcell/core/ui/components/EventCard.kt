package com.turkcell.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turkcell.core.domain.event.Event

@Composable
fun EventCard(event: Event){
    Column(modifier = Modifier.padding(12.dp)){
        Text(event.name)
        Text(event.venue)
        Text(event.startsAt)

        Text("Ticket Types:")
        event.TicketTypes.forEach {
            Text("- ${it.name} : ${it.priceCents}")
        }
    }
}