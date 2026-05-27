package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import com.turkcell.ticketapp.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.core.ui.theme.Outline
import com.turkcell.ticketapp.viewmodel.RegisterViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
){
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isRegistered){
        if (state.isRegistered)
            onRegisterSuccess()
    }

    //hata snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let{
            snackbarHostState.showSnackbar(it)
            viewModel.consumeError()
        }
    }

    var passwordVisible by remember { mutableStateOf(false)}
    var confirmPasswordVisible by remember { mutableStateOf(false)}



   Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

        ){
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(24.dp))

            //EMAIL
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(text = stringResource(R.string.email))},
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = state.email.isNotEmpty() && !state.isEmailValid,
                supportingText = {
                    if(state.email.isNotEmpty() && !state.isEmailValid){
                        Text(
                            text = stringResource(R.string.error_invalid_email),
                            color = MaterialTheme.colorScheme.error

                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            //ŞİFRE
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(stringResource(R.string.password))},
                singleLine = true,
                visualTransformation = if ( passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = {passwordVisible = !passwordVisible}){
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else
                            Icons.Default.Visibility,
                            contentDescription = null

                        )
                    }
                },

                isError = state.password.isNotEmpty() && !state.isPasswordValid,
                supportingText = {
                    if (state.password.isNotEmpty() && !state.isPasswordValid){
                        Text(
                            text = stringResource(R.string.error_password_min),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text(stringResource(R.string.label_confirm_password))},
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = {confirmPasswordVisible = !confirmPasswordVisible}){
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff
                            else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                isError = state.confirmPassword.isNotEmpty() && !state.passwordsMatch,
                supportingText = {
                    if ( state.confirmPassword.isNotEmpty() && !state.passwordsMatch){
                        Text(
                            text = stringResource(R.string.error_passwords_mismatch),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))

            if ( state.errorMessage != null){
                Spacer(Modifier.height(12.dp))
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = viewModel::submit,
                enabled = state.canSubmit,
                modifier = Modifier.fillMaxWidth()
            ){
                if ( state.isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current
                    )
                }
                else{
                    Text(stringResource(R.string.register_button))
                }
            }
            Spacer (Modifier.height(24.dp))

            TextButton(onClick = onNavigateToLogin){
                Text(stringResource(R.string.register_already_have_account))
            }



        }
    }
}