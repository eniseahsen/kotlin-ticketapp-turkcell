package com.turkcell.ticketapp.screen

import android.app.Activity
import android.graphics.Bitmap
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.turkcell.core.domain.ticket.TicketStatus
import com.turkcell.core.ui.theme.Surface
import com.turkcell.ticketapp.viewmodel.TicketDetailViewModel
import org.koin.androidx.compose.koinViewModel
import qrcode.QRCode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: TicketDetailViewModel = koinViewModel()
){
    val state by viewModel.state.collectAsState()
    // Ekran açıkken parlaklığı maksimuma çek
    // Kapanınca eski değere döndür — DisposableEffect bunu garantiliyor
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        val originalBrightness = window?.attributes?.screenBrightness ?: -1f
        window?.attributes = window?.attributes?.apply{
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
        onDispose{
            window?.attributes = window?.attributes?.apply{
                screenBrightness = originalBrightness
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bilet QR")},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ){ CircularProgressIndicator() }
            }
            state.error != null -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ){
                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                        Text(state.error!!)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = viewModel::loadTickets){
                            Text("Tekrar Dene")
                        }
                    }

                }
            }
            state.ticket != null -> {
                val ticket = state.ticket!!

                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    // qrCode UUID'sini QR bitmap'e çeviriyoruz
                    // Sadece qrCode alanı payload olarak kullanılıyor (API.MD §5.4)
                    val qrBitmap = remember(ticket.qrCode){
                        generateQrBitmap(ticket.qrCode)
                    }
                    qrBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Kod",
                            modifier = Modifier.size(280.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Bilet #${ticket.id.take(8).uppercase()}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    val (statusText, statusColor) = when (ticket.status){
                        TicketStatus.VALID -> "Geçerli" to MaterialTheme.colorScheme.primary
                        TicketStatus.USED -> "Kullanıldı" to MaterialTheme.colorScheme.outline
                        TicketStatus.CANCELLED -> "İptal Edildi" to MaterialTheme.colorScheme.error
                    }

                    Surface(
                        color = statusColor.copy(alpha=0.12f),
                        shape = MaterialTheme.shapes.medium
                    ){
                        Text(
                            text = statusText,
                            color = statusColor,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

}

private fun generateQrBitmap(content: String): Bitmap? = try{
    val qrCode = QRCode.ofSquares().build(content)
    val rendered = qrCode.render()
    // qrcode-kotlin Android'de nativeImage() Bitmap döndürür
    rendered.nativeImage() as? Bitmap
} catch (e: Exception){
    null
}


