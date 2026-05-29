package com.turkcell.ticketapp.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.core.domain.ticket.Ticket
import com.turkcell.core.domain.ticket.TicketStatus

import com.turkcell.ticketapp.R
import com.turkcell.ticketapp.viewmodel.MyTicketViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    onNavigateBack: () -> Unit,
    onTicketClick: (String) -> Unit,
    viewModel: MyTicketViewModel = koinViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_tickets_title))},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ){ innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = viewModel::loadTickets,
            modifier = Modifier.padding(innerPadding)
        ) {
            when{
                state.isLoading && state.tickets.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                        }
                    }


            state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Text(state.error!!)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = viewModel::loadTickets){
                        Text(stringResource(R.string.retry))
                    }
                }


            }
        }

            state.tickets.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(stringResource(R.string.no_tickets))
                }
        }
            else -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = state.tickets, key = {it.id}){ ticket ->
                    TicketCard(ticket = ticket, onClick = { onTicketClick(ticket.id)})
                }
            }
        }
        }
    }
}


}

@Composable
private fun TicketCard(ticket: Ticket, onClick: () -> Unit){
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick)

    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment =Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column(modifier = Modifier.weight(1f)){
                Text(ticket.id, style = MaterialTheme.typography.bodySmall)
                Text(ticket.ticketTypeId, style = MaterialTheme.typography.bodyMedium)

            }
            StatusChip(ticket.status)
        }
    }
}

@Composable
private fun StatusChip(status: TicketStatus){
    val(label, color) = when (status){
        TicketStatus.VALID -> Pair("Geçerli", MaterialTheme.colorScheme.primary)
        TicketStatus.USED -> Pair("Kullanıldı", MaterialTheme.colorScheme.outline)
        TicketStatus.CANCELLED -> Pair("İptal", MaterialTheme.colorScheme.error)
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = MaterialTheme.shapes.small
    ){
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}