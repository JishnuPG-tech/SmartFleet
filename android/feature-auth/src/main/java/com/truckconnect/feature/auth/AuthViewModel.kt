package com.truckconnect.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.truckconnect.core.data.AuthRepository
import com.truckconnect.core.network.NetworkResult
import com.truckconnect.core.network.model.AuthResponse
import com.truckconnect.core.network.model.DriverRegisterRequest
import com.truckconnect.core.network.model.RegisterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val auth: AuthResponse) : AuthUiState
    data class Registered(val result: RegisterResponse) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    fun loginAdmin(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthUiState.Error("Please enter both email and password")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authRepository.loginAdmin(email, pass)) {
                is NetworkResult.Success -> {
                    _authState.value = AuthUiState.Success(result.data)
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _authState.value = AuthUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _authState.value = AuthUiState.Error(result.throwable.localizedMessage ?: "Network error occurred")
                }
            }
        }
    }

    fun loginDriver(phone: String, pin: String, onSuccess: () -> Unit) {
        if (phone.isBlank() || pin.length < 4) {
            _authState.value = AuthUiState.Error("Please enter valid phone and 4-digit PIN")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authRepository.loginDriver(phone, pin)) {
                is NetworkResult.Success -> {
                    _authState.value = AuthUiState.Success(result.data)
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _authState.value = AuthUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _authState.value = AuthUiState.Error(result.throwable.localizedMessage ?: "Network error occurred")
                }
            }
        }
    }

    fun registerDriver(req: DriverRegisterRequest, onSuccess: () -> Unit) {
        if (req.name.isBlank() || req.phone.isBlank() || req.pin.length < 4 || req.licenceNo.isBlank()) {
            _authState.value = AuthUiState.Error("Please fill all required details and set a 4-digit PIN")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authRepository.registerDriver(req)) {
                is NetworkResult.Success -> {
                    _authState.value = AuthUiState.Registered(result.data)
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _authState.value = AuthUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _authState.value = AuthUiState.Error(result.throwable.localizedMessage ?: "Registration error")
                }
            }
        }
    }

    fun isUserLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }
}
