package com.tienda.tienda_virtual.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tienda.tienda_virtual.data.model.CartItemWithProduct
import com.tienda.tienda_virtual.ui.viewmodel.CartViewModel
import com.tienda.tienda_virtual.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    userId: String,
    onBackClick: () -> Unit,
    onCheckout: () -> Unit,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.getCartItemsWithProduct(userId).collectAsState(initial = emptyList())
    val totalAmount = cartItems.sumOf { it.cartItem.quantity * it.product.price }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Carrito de Compras") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )
        
        if (cartItems.isEmpty()) {
            // Empty cart
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrito vacío",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { cartItemWithProduct ->
                    CartItemCard(
                        cartItemWithProduct = cartItemWithProduct,
                        onQuantityChange = { newQuantity ->
                            cartViewModel.updateCartItemQuantity(
                                userId = userId,
                                productId = cartItemWithProduct.cartItem.productId,
                                quantity = newQuantity
                            )
                        },
                        onRemove = {
                            cartViewModel.removeFromCart(
                                userId = userId,
                                productId = cartItemWithProduct.cartItem.productId
                            )
                        }
                    )
                }
            }
            
            // Total and checkout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.2f", totalAmount)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Proceder al Pago")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItemWithProduct: CartItemWithProduct,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            AsyncImage(
                model = ImageUtils.getUriFromPath(cartItemWithProduct.product.imageUrl) 
                    ?: "https://via.placeholder.com/80x80",
                contentDescription = cartItemWithProduct.product.name,
                modifier = Modifier
                    .size(80.dp),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Product info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItemWithProduct.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                
                Text(
                    text = "$${String.format("%.2f", cartItemWithProduct.product.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { 
                            if (cartItemWithProduct.cartItem.quantity > 1) {
                                onQuantityChange(cartItemWithProduct.cartItem.quantity - 1)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Text(
                        text = cartItemWithProduct.cartItem.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    IconButton(
                        onClick = { 
                            if (cartItemWithProduct.cartItem.quantity < cartItemWithProduct.product.stock) {
                                onQuantityChange(cartItemWithProduct.cartItem.quantity + 1)
                            }
                        },
                        enabled = cartItemWithProduct.cartItem.quantity < cartItemWithProduct.product.stock,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            // Remove button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
