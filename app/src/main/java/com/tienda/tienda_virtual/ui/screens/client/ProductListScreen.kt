package com.tienda.tienda_virtual.ui.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tienda.tienda_virtual.data.model.Category
import com.tienda.tienda_virtual.data.model.ProductWithCategory
import com.tienda.tienda_virtual.ui.viewmodel.ProductViewModel
import com.tienda.tienda_virtual.utils.ImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit,
    productViewModel: ProductViewModel
) {
    val products by productViewModel.allProductsWithCategory.collectAsState(initial = emptyList())
    val categories by productViewModel.allCategories.collectAsState(initial = emptyList())
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    
    val filteredProducts = if (selectedCategoryId != null) {
        products.filter { it.product.categoryId == selectedCategoryId }
    } else {
        products
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar con gradiente
        TopAppBar(
            title = { 
                Text(
                    "Productos",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = {
                IconButton(onClick = onCartClick) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrito",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
        
        // Categories filter
        LazyRow(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryId == null,
                    onClick = { selectedCategoryId = null },
                    label = { Text("Todos") }
                )
            }
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { 
                        selectedCategoryId = if (selectedCategoryId == category.id) null else category.id
                    },
                    label = { Text(category.name) }
                )
            }
        }
        
        // Products grid
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredProducts) { productWithCategory ->
                ProductCard(
                    productWithCategory = productWithCategory,
                    onProductClick = { onProductClick(productWithCategory.product.id) }
                )
            }
        }
        }
    }
}

@Composable
fun ProductCard(
    productWithCategory: ProductWithCategory,
    onProductClick: () -> Unit
) {
    Card(
        onClick = onProductClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product image
            AsyncImage(
                model = ImageUtils.getUriFromPath(productWithCategory.product.imageUrl) 
                    ?: "https://via.placeholder.com/200x150",
                contentDescription = productWithCategory.product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Product info
            Text(
                text = productWithCategory.product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = productWithCategory.category.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = productWithCategory.product.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", productWithCategory.product.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Stock: ${productWithCategory.product.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (productWithCategory.product.stock > 0) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
