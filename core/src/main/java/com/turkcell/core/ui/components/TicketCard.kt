package com.turkcell.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turkcell.core.domain.Ticket

@Composable
fun TicketCard(ticket: Ticket) {
    Column(modifier = Modifier.padding(12.dp)){
        Text("Ticket ID: ${ticket.id}")
        Text("QR: ${ticket.qrCode}")
        Text("Status: ${ticket.status}")
    }
}