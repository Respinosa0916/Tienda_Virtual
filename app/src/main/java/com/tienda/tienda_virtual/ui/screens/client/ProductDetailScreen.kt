package com.tienda.tienda_virtual.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tienda.tienda_virtual.data.model.ProductWithCategory
import com.tienda.tienda_virtual.ui.viewmodel.ProductViewModel
import com.tienda.tienda_virtual.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onAddToCart: (String, Int) -> Unit,
    onEditProduct: () -> Unit,
    isVendor: Boolean = false,
    productViewModel: ProductViewModel
) {
    var quantity by remember { mutableStateOf(1) }
    val selectedProduct by productViewModel.selectedProduct.collectAsState()
    
    LaunchedEffect(productId) {
        productViewModel.getProductById(productId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Detalles del Producto") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )
        
        selectedProduct?.let { product ->
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Product image
                AsyncImage(
                    model = ImageUtils.getUriFromPath(product.imageUrl) 
                        ?: "https://via.placeholder.com/400x300",
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Product name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Price
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Stock status
                Card(
                    modifier = Modifier.padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (product.stock > 0) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = if (product.stock > 0) 
                            "Disponible - Stock: ${product.stock}" 
                        else 
                            "Sin stock",
                        modifier = Modifier.padding(12.dp),
                        color = if (product.stock > 0) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                
                // Description
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Quantity selector
                Card(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Cantidad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Disminuir"
                                )
                            }
                            
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            
                            IconButton(
                                onClick = { if (quantity < product.stock) quantity++ },
                                enabled = quantity < product.stock
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Aumentar"
                                )
                            }
                        }
                    }
                }
                
                // Botones según tipo de usuario
                if (isVendor) {
                    // Botón para editar (vendedor)
                    Button(
                        onClick = onEditProduct,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Editar Producto")
                    }
                } else {
                    // Botón para agregar al carrito (cliente)
                    Button(
                        onClick = { onAddToCart(product.id, quantity) },
                        enabled = product.stock > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Agregar al Carrito")
                    }
                }
            }
        } ?: run {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
