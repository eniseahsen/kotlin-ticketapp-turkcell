package com.turkcell.ticketapp.screen


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import com.turkcell.ticketapp.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource


@Composable
fun PurchaseConfirmDialog(
    totalCents: Long,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text= stringResource(R.string.confirm_payment))},
        text = {
            Text(
                text = stringResource(
                    R.string.payment_confirm_message,
                    totalCents / 100.0
                )
            )
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isLoading){
                if (isLoading){
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)

                }
                else{
                    Text("Onayla")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading){
                Text("İptal")
            }
        }



        )
}