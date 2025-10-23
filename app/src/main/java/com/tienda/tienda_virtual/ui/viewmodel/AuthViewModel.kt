package com.tienda.tienda_virtual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tienda.tienda_virtual.data.model.User
import com.tienda.tienda_virtual.data.model.UserType
import com.tienda.tienda_virtual.data.repository.AuthRepository
import com.tienda.tienda_virtual.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        // Cargar usuario guardado al inicializar
        loadSavedUser()
    }
    
    private fun loadSavedUser() {
        viewModelScope.launch {
            try {
                val userId = userPreferencesManager.userId.first()
                if (userId != null) {
                    val email = userPreferencesManager.userEmail.first()
                    val name = userPreferencesManager.userName.first()
                    val userTypeString = userPreferencesManager.userType.first()
                    val phone = userPreferencesManager.userPhone.first()
                    val address = userPreferencesManager.userAddress.first()
                    
                    if (email != null && name != null && userTypeString != null) {
                        val user = User(
                            id = userId,
                            email = email,
                            password = "", // No guardamos la contraseña
                            name = name,
                            userType = UserType.valueOf(userTypeString),
                            phone = phone,
                            address = address
                        )
                        
                        _uiState.value = _uiState.value.copy(
                            currentUser = user,
                            isLoggedIn = true
                        )
                    }
                }
            } catch (e: Exception) {
                // Si hay error al cargar, simplemente no hay usuario guardado
            }
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val user = authRepository.login(email, password)
                if (user != null) {
                    // Guardar usuario en DataStore
                    userPreferencesManager.saveUser(
                        userId = user.id,
                        email = user.email,
                        name = user.name,
                        userType = user.userType.name,
                        phone = user.phone,
                        address = user.address
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = user,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Credenciales inválidas"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }
    
    fun register(
        name: String,
        email: String,
        password: String,
        userType: UserType,
        phone: String? = null,
        address: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Check if user already exists
                val existingUser = authRepository.getUserByEmail(email)
                if (existingUser != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "El email ya está registrado"
                    )
                    return@launch
                }
                
                val user = authRepository.register(name, email, password, userType, phone, address)
                
                // Guardar usuario en DataStore
                userPreferencesManager.saveUser(
                    userId = user.id,
                    email = user.email,
                    name = user.name,
                    userType = user.userType.name,
                    phone = user.phone,
                    address = user.address
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentUser = user,
                    isLoggedIn = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrarse: ${e.message}"
                )
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            // Limpiar datos del DataStore
            userPreferencesManager.clearUser()
            _uiState.value = AuthUiState()
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
