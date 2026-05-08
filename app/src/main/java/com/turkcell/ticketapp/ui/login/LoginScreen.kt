package com.turkcell.ticketapp.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
){
    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}

    val message by viewModel.message.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp)
    ){
        OutlinedTextField(
            value = email,
            onValueChange = { email = it},
            label = { Text("Email")}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it},
            label = { Text("Şifre")}
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(email, password)
            }
        ){
            Text("Giriş Yap")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(message)
    }



}