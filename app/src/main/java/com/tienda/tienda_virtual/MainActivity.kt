package com.tienda.tienda_virtual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tienda.tienda_virtual.data.database.AppDatabase
import com.tienda.tienda_virtual.data.preferences.UserPreferencesManager
import com.tienda.tienda_virtual.data.repository.*
import com.tienda.tienda_virtual.data.sample.DatabaseInitializer
import com.tienda.tienda_virtual.navigation.AppNavigation
import com.tienda.tienda_virtual.ui.theme.TiendaVirtualTheme
import com.tienda.tienda_virtual.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiendaVirtualTheme {
                TiendaVirtualApp()
            }
        }
    }
}

@Composable
fun TiendaVirtualApp() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val userPreferencesManager = remember { UserPreferencesManager(context) }
    var isDataInitialized by remember { mutableStateOf(false) }
    
    // Initialize sample data once
    LaunchedEffect(Unit) {
        if (!isDataInitialized) {
            try {
                val initializer = DatabaseInitializer(database)
                initializer.initializeSampleData()
                isDataInitialized = true
            } catch (e: Exception) {
                android.util.Log.e("TiendaVirtualApp", "Error initializing data", e)
            }
        }
    }
    
    val authRepository = AuthRepository(database.userDao())
    val productRepository = ProductRepository(database.productDao(), database.categoryDao())
    val cartRepository = CartRepository(database.cartDao())
    val orderRepository = OrderRepository(database.orderDao(), database.cartDao(), database.productDao())
    
    val authViewModel: AuthViewModel = viewModel {
        AuthViewModel(authRepository, userPreferencesManager)
    }
    
    val uiState by authViewModel.uiState.collectAsState()
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AppNavigation(
            authViewModel = authViewModel,
            currentUser = uiState.currentUser,
            authRepository = authRepository,
            productRepository = productRepository,
            cartRepository = cartRepository,
            orderRepository = orderRepository
        )
    }
}