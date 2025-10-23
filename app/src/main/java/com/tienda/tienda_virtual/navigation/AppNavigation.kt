package com.tienda.tienda_virtual.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import android.util.Log
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tienda.tienda_virtual.data.model.User
import com.tienda.tienda_virtual.data.repository.*
import com.tienda.tienda_virtual.ui.screens.auth.LoginScreen
import com.tienda.tienda_virtual.ui.screens.auth.RegisterScreen
import com.tienda.tienda_virtual.ui.screens.client.CartScreen
import com.tienda.tienda_virtual.ui.screens.client.ProductDetailScreen
import com.tienda.tienda_virtual.ui.screens.client.ProductListScreen
import com.tienda.tienda_virtual.ui.screens.vendor.AddProductScreen
import com.tienda.tienda_virtual.ui.screens.vendor.EditProductScreen
import com.tienda.tienda_virtual.ui.screens.vendor.OrdersScreen
import com.tienda.tienda_virtual.ui.screens.vendor.VendorDashboardScreen
import com.tienda.tienda_virtual.ui.screens.ProfileScreen
import com.tienda.tienda_virtual.data.model.UserType
import com.tienda.tienda_virtual.ui.viewmodel.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: com.tienda.tienda_virtual.ui.viewmodel.AuthViewModel,
    currentUser: User? = null,
    authRepository: AuthRepository? = null,
    productRepository: ProductRepository? = null,
    cartRepository: CartRepository? = null,
    orderRepository: OrderRepository? = null
) {
    var activeUser by remember { mutableStateOf<User?>(currentUser) }
    val authUiState by authViewModel.uiState.collectAsState()

    fun routeFor(user: User): String = when (user.userType) {
        com.tienda.tienda_virtual.data.model.UserType.CLIENTE -> "client_products"
        com.tienda.tienda_virtual.data.model.UserType.VENDEDOR -> "vendor_dashboard"
    }

    // Keep activeUser synced to the single AuthViewModel state
    LaunchedEffect(authUiState.currentUser?.id, authUiState.isLoggedIn) {
        if (authUiState.isLoggedIn && authUiState.currentUser != null) {
            activeUser = authUiState.currentUser
        }
    }
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Auth screens
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    val user = authViewModel.uiState.value.currentUser
                    if (user != null) {
                        activeUser = user
                        try {
                            // Ensure required repos exist before navigating
                            if (routeFor(user) == "client_products" && productRepository == null) return@LoginScreen
                            if (routeFor(user) == "vendor_dashboard" && productRepository == null) return@LoginScreen
                            navController.navigate(routeFor(user))
                        } catch (e: Exception) {
                            Log.e("AppNavigation", "Navigation error after login", e)
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                authViewModel = authViewModel
            )
        }
        
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    val user = authViewModel.uiState.value.currentUser
                    if (user != null) {
                        activeUser = user
                        try {
                            if (routeFor(user) == "client_products" && productRepository == null) return@RegisterScreen
                            if (routeFor(user) == "vendor_dashboard" && productRepository == null) return@RegisterScreen
                            navController.navigate(routeFor(user))
                        } catch (e: Exception) {
                            Log.e("AppNavigation", "Navigation error after register", e)
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                },
                authViewModel = authViewModel
            )
        }
        
        // Client screens
        composable("client_products") {
            val productViewModel: ProductViewModel = viewModel {
                ProductViewModel(productRepository!!)
            }
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onLogout = {
                    authViewModel.logout()
                    activeUser = null
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                productViewModel = productViewModel
            )
        }
        
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val productViewModel: ProductViewModel = viewModel {
                ProductViewModel(productRepository!!)
            }
            val cartViewModel: CartViewModel = viewModel {
                CartViewModel(cartRepository!!)
            }
            val isVendor = activeUser?.userType == UserType.VENDEDOR
            
            ProductDetailScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddToCart = { prodId, quantity ->
                    activeUser?.let { user ->
                        cartViewModel.addToCart(user.id, prodId, quantity)
                    }
                    navController.popBackStack()
                },
                onEditProduct = {
                    navController.navigate("edit_product/$productId")
                },
                isVendor = isVendor,
                productViewModel = productViewModel
            )
        }
        
        composable("edit_product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val productViewModel: ProductViewModel = viewModel {
                ProductViewModel(productRepository!!)
            }
            
            // Cargar el producto primero
            LaunchedEffect(productId) {
                productViewModel.getProductById(productId)
            }
            
            val selectedProduct by productViewModel.selectedProduct.collectAsState()
            
            selectedProduct?.let { product ->
                // Necesitamos obtener el ProductWithCategory
                val productsWithCategory by productViewModel.allProductsWithCategory.collectAsState()
                val productWithCategory = productsWithCategory.find { it.product.id == productId }
                
                productWithCategory?.let {
                    EditProductScreen(
                        productWithCategory = it,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onProductUpdated = {
                            navController.popBackStack()
                        },
                        onProductDeleted = {
                            navController.navigate("vendor_dashboard") {
                                popUpTo("vendor_dashboard") { inclusive = true }
                            }
                        },
                        productViewModel = productViewModel
                    )
                }
            }
        }
        
        composable("cart") {
            val cartViewModel: CartViewModel = viewModel {
                CartViewModel(cartRepository!!)
            }
            CartScreen(
                userId = activeUser?.id ?: "",
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckout = {
                    navController.navigate("checkout")
                },
                cartViewModel = cartViewModel
            )
        }
        
        composable("checkout") {
            val cartViewModel: CartViewModel = viewModel {
                CartViewModel(cartRepository!!)
            }
            val orderViewModel: OrderViewModel = viewModel {
                OrderViewModel(orderRepository!!)
            }
            com.tienda.tienda_virtual.ui.screens.client.CheckoutScreen(
                userId = activeUser?.id ?: "",
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderPlaced = {
                    navController.navigate("client_products") {
                        popUpTo("client_products") { inclusive = true }
                    }
                },
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel
            )
        }
        
        // Vendor screens
        composable("vendor_dashboard") {
            val productViewModel: ProductViewModel = viewModel {
                ProductViewModel(productRepository!!)
            }
            VendorDashboardScreen(
                vendorId = activeUser?.id ?: "",
                onAddProductClick = {
                    navController.navigate("add_product")
                },
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onOrdersClick = {
                    navController.navigate("orders")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onLogout = {
                    authViewModel.logout()
                    activeUser = null
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                productViewModel = productViewModel
            )
        }
        
        // Profile screen (shared for both user types)
        composable("profile") {
            activeUser?.let { user ->
                ProfileScreen(
                    user = user,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveProfile = { name, phone, address ->
                        // Update user profile
                        val updatedUser = user.copy(
                            name = name,
                            phone = phone,
                            address = address
                        )
                        activeUser = updatedUser
                        // Here you would also call authRepository.updateUser(updatedUser)
                    },
                    authViewModel = authViewModel
                )
            }
        }
        
        composable("add_product") {
            val productViewModel: ProductViewModel = viewModel {
                ProductViewModel(productRepository!!)
            }
            AddProductScreen(
                vendorId = activeUser?.id ?: "",
                onBackClick = {
                    navController.popBackStack()
                },
                onProductAdded = {
                    navController.popBackStack()
                },
                productViewModel = productViewModel
            )
        }
        
        composable("orders") {
            val orderViewModel: OrderViewModel = viewModel {
                OrderViewModel(orderRepository!!)
            }
            OrdersScreen(
                vendorId = activeUser?.id ?: "",
                onBackClick = {
                    navController.popBackStack()
                },
                orderViewModel = orderViewModel
            )
        }
    }
}
