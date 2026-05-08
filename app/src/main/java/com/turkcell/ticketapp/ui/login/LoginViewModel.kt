package com.turkcell.ticketapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//bağımlılığı dışarıdan almak = dependency injection
class LoginViewModel(private val authRepository : AuthRepository) : ViewModel(){
                     //repository injection. viewmodel dışarıdan repository istiyor, kendisi oluşturmuyor
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message


    fun login(
        email: String,
        password: String
    ){
        viewModelScope.launch {
            val result = authRepository.login(email, password)

            result.onSuccess {
                _message.value = "Başarılı Giriş"
            }

            result.onFailure {
                _message.value = "Hata Oluştu"
            }
        }
    }

}