package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.turkcell.ticketapp.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.ticketapp.viewmodel.EventDetailViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.turkcell.core.domain.event.TicketType
import com.turkcell.core.util.DateFormatter
import com.turkcell.ticketapp.viewmodel.PurchaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    onNavigateBack: () -> Unit,
    onPurchaseComplete: () -> Unit,
    viewModel: EventDetailViewModel = koinViewModel(),
    purchaseViewModel: PurchaseViewModel = koinViewModel()
){
    val state by viewModel.state.collectAsStateWithLifecycle()
    val purchaseState by purchaseViewModel.state.collectAsStateWithLifecycle()

    // Ödeme başarılı → Biletlerim'e git
    LaunchedEffect(purchaseState.isPaid){
        if (purchaseState.isPaid) onPurchaseComplete()
    }

    // capacity_exceeded → etkinliği yenile
    LaunchedEffect(purchaseState.shouldRefreshEvent) {
        if (purchaseState.shouldRefreshEvent){
            viewModel.loadEvent()
            purchaseViewModel.consumeRefreshEvent()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(purchaseState.errorMessage) {
        purchaseState.errorMessage?.let{
            snackbarHostState.showSnackbar(it)
            purchaseViewModel.consumeError()
        }
    }

    // Ödeme onay diyaloğu
    if (purchaseState.pendingPurchaseId != null){
        PurchaseConfirmDialog(
            totalCents = state.totalCents,
            isLoading = purchaseState.isLoading,
            onConfirm = purchaseViewModel::confirmPayment,
            onDismiss = purchaseViewModel:: dismissDialog
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.event?.name ?: stringResource(R.string.event_detail_title))},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            if (state.event != null) {
                Surface(shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.total_price, state.totalCents / 100.0),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = { purchaseViewModel.createPurchase(state.selectedQuantities) },
                            enabled = state.canPurchase

                        ) {
                            Text(stringResource(R.string.purchase_button))
                        }

                    }
                }

            }

            }){ innerPadding ->
                PullToRefreshBox(
                    isRefreshing = state.isLoading,
                    onRefresh = viewModel::loadEvent,
                    modifier = Modifier.padding(innerPadding)
                ){
                    when{
                        state.isLoading && state.event == null -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                CircularProgressIndicator()
                            }
                        }
                        state.error != null && state.event == null -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(state.error!!)
                                    Spacer(Modifier.height(8.dp))
                                    Button(onClick = viewModel::loadEvent){
                                        Text(stringResource(R.string.retry))
                                    }
                                }
                            }
                        }
                        state.event != null -> {
                            val event = state.event!!
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ){
                                item{
                                    Text(event.name, style = MaterialTheme.typography.headlineSmall)
                                    Spacer(Modifier.height(4.dp))
                                    Text(event.venue, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(4.dp))
                                    event.startsAt?.let{
                                        Text(
                                            stringResource(R.string.event_starts_at, DateFormatter.format(it)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    event.endsAt?.let {
                                        Text(
                                            stringResource(R.string.event_ends_at, DateFormatter.format(it)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(event.description, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        stringResource(R.string.ticket_types_header),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                items(items = event.ticketTypes, key = {it.id}){ tt ->
                                    TicketTypeRow(
                                        ticketType = tt,
                                        quantity = state.selectedQuantities[tt.id] ?: 0,
                                        onIncrease = { viewModel.increaseQuantity(tt.id)},
                                        onDecrease = { viewModel.decreaseQuantity(tt.id)}
                                    )

                                }
                            }

                        }
                    }
                    }
                }
                }

@Composable
private fun TicketTypeRow(
    ticketType: TicketType,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ticketType.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    stringResource(R.string.ticket_capacity_info,
                        ticketType.remaining,
                        ticketType.capacity
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    stringResource(R.string.ticket_price, ticketType.priceCents / 100.0),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, enabled = quantity > 0) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.widthIn(min = 24.dp),
                )
                IconButton(
                    onClick = onIncrease,
                    enabled = quantity < minOf(20, ticketType.remaining.toInt())
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }
}